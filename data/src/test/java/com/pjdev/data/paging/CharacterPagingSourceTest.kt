package com.pjdev.data.paging

import androidx.paging.PagingSource
import com.pjdev.data.remote.api.RickAndMortyApi
import com.pjdev.data.remote.dto.CharacterDto
import com.pjdev.data.remote.dto.CharacterResponseDto
import com.pjdev.data.remote.dto.EpisodeDto
import com.pjdev.data.remote.dto.LocationDto
import com.pjdev.data.remote.dto.PageInfoDto
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class CharacterPagingSourceTest {

    @Test
    fun `load returns first page with next key`() = runTest {
        val api = FakeRickAndMortyApi(
            response = createCharacterResponse(
                next = "https://rickandmortyapi.com/api/character?page=2",
                prev = null,
            ),
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        val page = result as PagingSource.LoadResult.Page

        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
        assertEquals(1, page.data.size)
        assertEquals("Rick Sanchez", page.data.first().name)
    }

    @Test
    fun `load returns no next key on last page`() = runTest {
        val api = FakeRickAndMortyApi(
            response = createCharacterResponse(
                next = null,
                prev = "https://rickandmortyapi.com/api/character?page=1",
            ),
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        val page = result as PagingSource.LoadResult.Page

        assertEquals(1, page.prevKey)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `load returns error when network request fails`() = runTest {
        val expectedException = IOException("Network error")

        val api = FakeRickAndMortyApi(
            exception = expectedException,
        )

        val pagingSource = CharacterPagingSource(
            api = api,
            name = null,
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error)

        val error = result as PagingSource.LoadResult.Error

        assertEquals(expectedException, error.throwable)
    }

    private fun createCharacterResponse(
        next: String?,
        prev: String?,
    ): CharacterResponseDto {
        return CharacterResponseDto(
            info = PageInfoDto(
                next = next,
                prev = prev,
            ),
            results = listOf(
                CharacterDto(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    origin = LocationDto(name = "Earth"),
                    location = LocationDto(name = "Earth"),
                    image = "https://example.com/rick.jpg",
                    episode = listOf(
                        "https://rickandmortyapi.com/api/episode/1",
                    ),
                ),
            ),
        )
    }

    private class FakeRickAndMortyApi(
        private val response: CharacterResponseDto? = null,
        private val exception: IOException? = null,
    ) : RickAndMortyApi {

        override suspend fun getCharacters(
            page: Int,
            name: String?,
        ): CharacterResponseDto {
            exception?.let { throw it }

            return requireNotNull(response)
        }

        override suspend fun getCharacter(
            id: Int,
        ): CharacterDto {
            error("Not required for this test")
        }

        override suspend fun getEpisode(
            id: Int,
        ): EpisodeDto {
            error("Not required for this test")
        }

        override suspend fun getEpisodes(
            ids: String,
        ): List<EpisodeDto> {
            error("Not required for this test")
        }
    }
}
