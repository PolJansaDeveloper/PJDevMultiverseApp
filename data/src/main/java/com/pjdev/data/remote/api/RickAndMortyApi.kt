package com.pjdev.data.remote.api

import com.pjdev.data.remote.dto.CharacterDto
import com.pjdev.data.remote.dto.CharacterResponseDto
import com.pjdev.data.remote.dto.EpisodeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String?,
    ): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int,
    ): CharacterDto

    @GET("episode/{id}")
    suspend fun getEpisode(
        @Path("id") id: Int,
    ): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodes(
        @Path("ids") ids: String,
    ): List<EpisodeDto>
}
