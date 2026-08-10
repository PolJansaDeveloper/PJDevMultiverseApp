package com.pjdev.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pjdev.data.remote.api.RickAndMortyApi
import com.pjdev.data.remote.mapper.toCharacter
import com.pjdev.domain.model.Character
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import com.pjdev.data.remote.error.toDomainFailure

class CharacterPagingSource(
    private val api: RickAndMortyApi,
    private val name: String?,
) : PagingSource<Int, Character>() {

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, Character> {
        return try {
            val page = params.key ?: INITIAL_PAGE

            val response = api.getCharacters(
                page = page,
                name = name,
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
    }
}
