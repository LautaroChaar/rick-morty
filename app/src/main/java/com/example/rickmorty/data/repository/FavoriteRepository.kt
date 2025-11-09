package com.example.rickmorty.data.repository

import android.content.Context
import com.example.rickmorty.data.favorites.AppDatabase
import com.example.rickmorty.data.model.FavoriteCharacter

class FavoriteRepository(context: Context) {
    private val favoriteDao = AppDatabase.getInstance(context).favoriteDao()

    suspend fun getAllFavorites() = favoriteDao.getAllFavorites()

    suspend fun addToFavorites(character: FavoriteCharacter) =
        favoriteDao.addToFavorites(character)

    suspend fun removeFromFavorites(character: FavoriteCharacter) =
        favoriteDao.removeFromFavorites(character)

    suspend fun isFavorite(id: Int) = favoriteDao.isFavorite(id)
}
