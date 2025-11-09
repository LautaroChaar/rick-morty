package com.example.rickmorty.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickmorty.data.model.FavoriteCharacter
import com.example.rickmorty.data.model.toCharacter
import com.example.rickmorty.data.repository.FavoriteRepository
import com.example.rickmorty.databinding.ActivityFavoritesBinding
import com.example.rickmorty.ui.detail.CharacterDetailActivity
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var repository: FavoriteRepository
    private var favorites: List<FavoriteCharacter> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = FavoriteRepository(this)

        setSupportActionBar(binding.toolbarFavorites)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarFavorites.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            favorites = repository.getAllFavorites()
            if (favorites.isEmpty()) {
                showEmptyFavorites()
            } else {
                hideEmptyFavorites()
                val adapter = FavoriteAdapter(favorites) { character ->
                    val intent = Intent(this@FavoritesActivity, CharacterDetailActivity::class.java)
                    intent.putExtra(
                        CharacterDetailActivity.EXTRA_CHARACTER,
                        character.toCharacter()
                    )
                    startActivity(intent)
                }
                binding.rvFavorites.adapter = adapter
            }
        }
    }

    private fun showEmptyFavorites() {
        binding.layoutEmptyFavorites.visibility = View.VISIBLE
        binding.rvFavorites.visibility = View.GONE
    }

    private fun hideEmptyFavorites() {
        binding.layoutEmptyFavorites.visibility = View.GONE
        binding.rvFavorites.visibility = View.VISIBLE
    }
}
