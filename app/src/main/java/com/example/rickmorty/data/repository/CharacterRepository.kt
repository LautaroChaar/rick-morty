package com.example.rickmorty.data.repository

import com.example.rickmorty.data.api.ApiClient
import com.example.rickmorty.data.model.CharacterResponse
import com.example.rickmorty.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Handle data operations
class CharacterRepository {

    private val apiService = ApiClient.apiService

    // Fetches characters from the API
    suspend fun getCharacters(page: Int = 1): Resource<CharacterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCharacters(page)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("Empty response body")
                } else {
                    Resource.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Resource.Error("Network error: ${e.message ?: "Unknown error"}")
            }
        }
    }

    suspend fun getCharactersFiltered(
        name: String? = null,
        status: String? = null,
        page: Int = 1
    ): Resource<CharacterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCharactersFiltered(name, status, page)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("No characters found")
                } else {
                    if (response.code() == 404) {
                        Resource.Error("No characters found with the given filters")
                    } else {
                        Resource.Error("Error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Resource.Error("Network error: ${e.message ?: "Unknown error"}")
            }
        }
    }

}