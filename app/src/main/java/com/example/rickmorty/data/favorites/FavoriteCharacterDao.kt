package com.example.rickmorty.data.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmorty.data.model.FavoriteCharacter

@Dao
interface FavoriteCharacterDao {

    @Query("SELECT * FROM favorite_characters")
    suspend fun getAllFavorites(): List<FavoriteCharacter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(character: FavoriteCharacter)

    @Delete
    suspend fun removeFromFavorites(character: FavoriteCharacter)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_characters WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}
