package com.example.rickmorty.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// Representing a character from Rick and Morty API
@Parcelize
data class Character(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("species")
    val species: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("origin")
    val origin: Location,

    @SerializedName("location")
    val location: Location,

    @SerializedName("image")
    val image: String,

    @SerializedName("episode")
    val episode: List<String>,

    @SerializedName("url")
    val url: String,

    @SerializedName("created")
    val created: String
) : Parcelable

// Representing a location in Rick and Morty universe
@Parcelize
data class Location(
    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
) : Parcelable

// API response wrapper containing pagination info and results
data class CharacterResponse(
    @SerializedName("info")
    val info: Info,

    @SerializedName("results")
    val results: List<Character>
)

// Pagination information from API
data class Info(
    @SerializedName("count")
    val count: Int,

    @SerializedName("pages")
    val pages: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("prev")
    val prev: String?
)