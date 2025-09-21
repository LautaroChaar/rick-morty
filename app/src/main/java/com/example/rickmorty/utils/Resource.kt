package com.example.rickmorty.utils

// Handling API responses. Consistent way to handle success, error, and loading states
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // Successful data fetch
    class Success<T>(data: T) : Resource<T>(data)

    // Error state with optional message
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    // Represents loading state
    class Loading<T> : Resource<T>()
}