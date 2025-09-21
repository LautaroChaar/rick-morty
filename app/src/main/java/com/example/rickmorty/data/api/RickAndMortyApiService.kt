package com.example.rickmorty.data.api

import com.example.rickmorty.data.model.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Rick and Morty API endpoints
interface RickAndMortyApiService {

    // Fetches list of characters
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): Response<CharacterResponse>

    @GET("character")
    suspend fun getCharactersFiltered(
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("page") page: Int
    ): Response<CharacterResponse>

}