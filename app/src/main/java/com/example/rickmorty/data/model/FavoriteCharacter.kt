package com.example.rickmorty.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a favorite character stored locally in Room.
 * Only essential fields are kept to display character info offline.
 */
@Entity(tableName = "favorite_characters")
data class FavoriteCharacter(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val originName: String,
    val locationName: String
)