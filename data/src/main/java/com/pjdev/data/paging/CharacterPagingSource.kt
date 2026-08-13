package com.pjdev.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.error.toDomainFailure
import com.pjdev.data.source.remote.mapper.toCharacter
import com.pjdev.domain.model.Character
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import kotlin.time.Duration.Companion.milliseconds

class CharacterPagingSource(
    private val api: RickAndMortyApi,
    private val name: String?,
) : PagingSource<Int, Character>() {

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: INITIAL_PAGE

            val response = loadPageWithRateLimitRetry(
                page = page,
            )

            LoadResult.Page(
                data = response.results.map { it.toCharacter() },
                prevKey = if (page == INITIAL_PAGE) null else page - 1,

                // The API exposes pagination through nullable next/previous URLs.
                // Paging only needs the next numeric key, so API URLs do not leak further.
                nextKey = if (response.info.next == null) null else page + 1,
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception.toDomainFailure())
        } catch (exception: HttpException) {
            LoadResult.Error(exception.toDomainFailure())
        } catch (exception: SerializationException) {
            LoadResult.Error(exception.toDomainFailure())
        }
    }

    private suspend fun loadPageWithRateLimitRetry(
        page: Int,
    ) = try {
        api.getCharacters(
            page = page,
            name = name,
        )
    } catch (exception: HttpException) {
        if (exception.code() != HTTP_TOO_MANY_REQUESTS) {
            throw exception
        }

        // A short bounded backoff absorbs temporary API throttling without
        // hiding persistent failures from the user.
        delay(
            rateLimitRetryDelay(
                exception = exception,
            ).milliseconds,
        )

        api.getCharacters(
            page = page,
            name = name,
        )
    }

    private fun rateLimitRetryDelay(
        exception: HttpException,
    ): Long {
        val retryAfterSeconds = exception
            .response()
            ?.headers()
            ?.get(RETRY_AFTER_HEADER)
            ?.toLongOrNull()

        return retryAfterSeconds
            ?.times(MILLIS_PER_SECOND)
            ?.coerceIn(
                minimumValue = MIN_RETRY_DELAY_MILLIS,
                maximumValue = MAX_RETRY_DELAY_MILLIS,
            )
            ?: DEFAULT_RETRY_DELAY_MILLIS
    }

    override fun getRefreshKey(
        state: PagingState<Int, Character>,
    ): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)

        // Rebuild the current page key around the user's closest loaded position.
        return anchorPage?.prevKey?.plus(1)
            ?: anchorPage?.nextKey?.minus(1)
    }

    private companion object {
        const val INITIAL_PAGE = 1

        const val HTTP_TOO_MANY_REQUESTS = 429
        const val RETRY_AFTER_HEADER = "Retry-After"

        const val MILLIS_PER_SECOND = 1_000L
        const val DEFAULT_RETRY_DELAY_MILLIS = 1_000L
        const val MIN_RETRY_DELAY_MILLIS = 750L
        const val MAX_RETRY_DELAY_MILLIS = 3_000L
    }
}
