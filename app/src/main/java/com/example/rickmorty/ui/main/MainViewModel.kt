package com.example.rickmorty.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickmorty.data.model.Character
import com.example.rickmorty.data.repository.CharacterRepository
import com.example.rickmorty.utils.Resource
import kotlinx.coroutines.launch

// ViewModel for MainActivity
class MainViewModel : ViewModel() {

    private val repository = CharacterRepository()

    // LiveData for characters list
    private val _characters = MutableLiveData<Resource<List<Character>>>()
    val characters: LiveData<Resource<List<Character>>> = _characters

    // Pagination tracking
    private var currentPage = 1
    private var totalPages = 1
    private var isLastPage = false
    private var isLoading = false

    // Current search query
    private var currentSearchQuery: String? = null

    // Current filter status
    private var currentStatusFilter: String? = null

    // All loaded characters (for pagination)
    private val allCharacters = mutableListOf<Character>()

    init {
        // Load initial data when ViewModel is created
        loadCharacters()
    }

    // Loads characters from the API
    fun loadCharacters(loadMore: Boolean = false) {
        if (isLoading || (loadMore && isLastPage)) return

        viewModelScope.launch {
            isLoading = true

            if (!loadMore) {
                _characters.value = Resource.Loading()
                currentPage = 1
                allCharacters.clear()
            }

            val result = repository.getCharactersFiltered(
                name = currentSearchQuery.takeIf { !it.isNullOrEmpty() },
                status = currentStatusFilter.takeIf { !it.isNullOrEmpty() },
                page = currentPage
            )

            when (result) {
                is Resource.Success -> {
                    result.data?.let { response ->
                        totalPages = response.info.pages
                        isLastPage = currentPage >= totalPages

                        allCharacters.addAll(response.results)
                        _characters.value = Resource.Success(allCharacters.toList())

                        if (loadMore) {
                            currentPage++
                        }
                    }
                }
                is Resource.Error -> {
                    _characters.value = Resource.Error(result.message ?: "Unknown error")
                }
                else -> {}
            }

            isLoading = false
        }
    }

    // Searches characters by name
    fun searchCharacters(query: String) {
        currentSearchQuery = query
        currentPage = 1
        allCharacters.clear()
        loadCharacters()
    }

    // Filters characters by status
    fun filterByStatus(status: String?) {
        currentStatusFilter = status
        currentPage = 1
        allCharacters.clear()
        loadCharacters()
    }

    // Clears all filters and reloads data
    fun clearFilters() {
        currentSearchQuery = null
        currentStatusFilter = null
        currentPage = 1
        allCharacters.clear()
        loadCharacters()
    }

    // Loads next page of characters
    fun loadNextPage() {
        if (!isLoading && !isLastPage) {
            currentPage++
            loadCharacters(true)
        }
    }

    // Refreshes the character list
    fun refresh() {
        currentPage = 1
        allCharacters.clear()
        loadCharacters()
    }

    // Checks if currently loading data
    fun isLoading(): Boolean = isLoading

    // Checks if on last page
    fun isLastPage(): Boolean = isLastPage
}