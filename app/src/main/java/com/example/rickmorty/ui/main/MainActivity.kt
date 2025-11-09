package com.example.rickmorty.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmorty.R
import com.example.rickmorty.data.model.Character
import com.example.rickmorty.databinding.ActivityMainBinding
import com.example.rickmorty.ui.detail.CharacterDetailActivity
import com.example.rickmorty.ui.favorites.FavoritesActivity
import com.example.rickmorty.utils.Resource
import kotlinx.coroutines.*

// Main Activity displaying the list of Rick and Morty characters
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var characterAdapter: CharacterAdapter

    // Coroutine job for search debounce
    private var searchJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    // Sets up the user interface components
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)

        // Configure SwipeRefreshLayout colors
        binding.swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        )
    }

    // Configures the RecyclerView with adapter and layout manager
    private fun setupRecyclerView() {
        characterAdapter = CharacterAdapter { character ->
            navigateToDetail(character)
        }

        binding.rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)

            // Add scroll listener for pagination
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Load more if we're at the end of the list
                    if (!viewModel.isLoading() && !viewModel.isLastPage()) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            })
        }
    }

    // Sets up LiveData observers for ViewModel
    private fun setupObservers() {
        // Observe character list changes
        viewModel.characters.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                    hideError()
                }
                is Resource.Success -> {
                    showLoading(false)
                    hideError()

                    resource.data?.let { characters ->
                        characterAdapter.updateCharacters(characters)

                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
            }
        }
    }

    // Sets up click listeners and text watchers
    private fun setupListeners() {
        // Search functionality with debounce
        binding.etSearch.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                val query = text?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    viewModel.clearFilters()
                } else {
                    viewModel.searchCharacters(query)
                }
            }
        }

        // Filter button
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    // Shows filter dialog for character status
    private fun showFilterDialog() {
        val statusOptions = arrayOf("All", "Alive", "Dead", "Unknown")
        var selectedOption = 0

        AlertDialog.Builder(this)
            .setTitle("Filter by Status")
            .setSingleChoiceItems(statusOptions, selectedOption) { _, which ->
                selectedOption = which
            }
            .setPositiveButton("Apply") { _, _ ->
                when (selectedOption) {
                    0 -> viewModel.clearFilters()
                    1 -> viewModel.filterByStatus("alive")
                    2 -> viewModel.filterByStatus("dead")
                    3 -> viewModel.filterByStatus("unknown")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Navigates to character detail screen
    private fun navigateToDetail(character: Character) {
        val intent = Intent(this, CharacterDetailActivity::class.java).apply {
            putExtra(CharacterDetailActivity.EXTRA_CHARACTER, character)
        }
        startActivity(intent)
    }

    // Shows or hides loading indicator
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Shows error state with message
    private fun showError(message: String) {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_error)
        drawable?.setBounds(0, 0, 224, 224)
        binding.tvError.setCompoundDrawables(null, drawable, null, null)
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
        binding.rvCharacters.visibility = View.GONE
    }

    // Hides error state
    private fun hideError() {
        binding.tvError.visibility = View.GONE
        binding.rvCharacters.visibility = View.VISIBLE
    }

}