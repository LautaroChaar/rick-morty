package com.example.rickmorty.ui.detail

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmorty.R
import com.example.rickmorty.data.model.Character
import com.example.rickmorty.data.model.FavoriteCharacter
import com.example.rickmorty.data.repository.FavoriteRepository
import com.example.rickmorty.databinding.ActivityCharacterDetailBinding
import kotlinx.coroutines.launch
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.graphics.toColorInt

class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterDetailBinding
    private lateinit var favoriteRepository: FavoriteRepository

    private var character: Character? = null
    private var isFavorite = false

    companion object {
        const val EXTRA_CHARACTER = "extra_character"
        const val EXTRA_REMOVED_FAVORITE_ID = "extra_removed_favorite_id"
        private const val TAG = "CharacterDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteRepository = FavoriteRepository(this)

        // Get character from intent
        character = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_CHARACTER, Character::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_CHARACTER)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting character from intent", e)
            null
        }

        if (character == null) {
            Toast.makeText(this, "Error loading character details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        displayCharacterData()
        checkIfFavorite()

        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = character?.name ?: "Character Details"
        }

        binding.collapsingToolbar.apply {
            title = character?.name ?: "Character Details"
            setExpandedTitleColor(ContextCompat.getColor(this@CharacterDetailActivity, android.R.color.white))
            setCollapsedTitleTextColor(ContextCompat.getColor(this@CharacterDetailActivity, android.R.color.white))
        }
    }

    private fun displayCharacterData() {
        character?.let { char ->
            try {
                Glide.with(this)
                    .load(char.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_character)
                    .error(R.drawable.placeholder_character)
                    .into(binding.ivCharacterImage)

                binding.tvCharacterName.text = char.name
                binding.tvGender.text = char.gender.ifEmpty { "Unknown" }
                binding.tvSpecies.text = char.species.ifEmpty { "Unknown" }

                val statusColor = when (char.status.lowercase()) {
                    "alive" -> R.color.status_alive
                    "dead" -> R.color.status_dead
                    else -> R.color.status_unknown
                }

                val circleDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ContextCompat.getColor(this@CharacterDetailActivity, statusColor))
                }
                binding.viewStatusIndicator.background = circleDrawable
                binding.tvStatus.text = getString(R.string.status, char.status, char.species)

                if (char.type.isNotEmpty()) {
                    binding.llType.visibility = View.VISIBLE
                    binding.tvType.text = char.type
                } else {
                    binding.llType.visibility = View.GONE
                }

                binding.tvOrigin.text = char.origin.name.ifEmpty { "Unknown" }
                binding.tvLocation.text = char.location.name.ifEmpty { "Unknown" }

                val episodeCount = char.episode.size
                binding.tvEpisodeCount.text = getString(
                    R.string.episodesCount,
                    episodeCount,
                    if (episodeCount == 1) "episode" else "episodes"
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error displaying character data", e)
                Toast.makeText(this, "Error displaying character details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfFavorite() {
        lifecycleScope.launch {
            character?.let {
                isFavorite = favoriteRepository.isFavorite(it.id)
                updateFavoriteIcon()
            }
        }
    }

    private fun toggleFavorite() {
        character?.let { char ->
            lifecycleScope.launch {
                val entity = FavoriteCharacter(
                    id = char.id,
                    name = char.name,
                    status = char.status,
                    species = char.species,
                    gender = char.gender,
                    image = char.image,
                    originName = char.origin.name,
                    locationName = char.location.name
                )

                if (isFavorite) {
                    favoriteRepository.removeFromFavorites(entity)
                    isFavorite = false
                    Toast.makeText(this@CharacterDetailActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    // Inform FavoritesActivity that this favorite was removed
                    val intent = Intent()
                    intent.putExtra(EXTRA_REMOVED_FAVORITE_ID, char.id)
                    setResult(Activity.RESULT_OK, intent)
                } else {
                    favoriteRepository.addToFavorites(entity)
                    isFavorite = true
                    Toast.makeText(this@CharacterDetailActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                    // If added, no need to update FavoritesActivity
                    setResult(Activity.RESULT_CANCELED)
                }
                updateFavoriteIcon()
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf("#D53641".toColorInt())
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.BLACK)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
