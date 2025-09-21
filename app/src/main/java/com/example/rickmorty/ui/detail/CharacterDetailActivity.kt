package com.example.rickmorty.ui.detail

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmorty.R
import com.example.rickmorty.data.model.Character
import com.example.rickmorty.databinding.ActivityCharacterDetailBinding

// Detail Activity with information about a character
class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterDetailBinding
    private var character: Character? = null

    companion object {
        const val EXTRA_CHARACTER = "extra_character"
        private const val TAG = "CharacterDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get character
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

        // Check if character was received
        if (character == null) {
            Toast.makeText(this, "Error loading character details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        displayCharacterData()
    }

    // Configures the user interface components
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = character?.name ?: "Character Details"
        }

        // Configure collapsing toolbar
        binding.collapsingToolbar.apply {
            title = character?.name ?: "Character Details"
            setExpandedTitleColor(ContextCompat.getColor(this@CharacterDetailActivity, android.R.color.white))
            setCollapsedTitleTextColor(ContextCompat.getColor(this@CharacterDetailActivity, android.R.color.white))
        }
    }

    // Displays character information
    private fun displayCharacterData() {
        character?.let { char ->
            try {
                // Load character image
                Glide.with(this)
                    .load(char.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_character)
                    .error(R.drawable.placeholder_character)
                    .into(binding.ivCharacterImage)

                // Set character name
                binding.tvCharacterName.text = char.name

                // Set status
                val statusColor = when (char.status.lowercase()) {
                    "alive" -> R.color.status_alive
                    "dead" -> R.color.status_dead
                    else -> R.color.status_unknown
                }

                // Create circular drawable
                val circleDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ContextCompat.getColor(this@CharacterDetailActivity, statusColor))
                }
                binding.viewStatusIndicator.background = circleDrawable

                binding.tvStatus.text = getString(R.string.status, char.status, char.species)

                // Set character information
                binding.tvGender.text = char.gender.ifEmpty { "Unknown" }
                binding.tvSpecies.text = char.species.ifEmpty { "Unknown" }

                // Set type if available
                if (char.type.isNotEmpty()) {
                    binding.llType.visibility = View.VISIBLE
                    binding.tvType.text = char.type
                } else {
                    binding.llType.visibility = View.GONE
                }

                // Set location information
                binding.tvOrigin.text = if (char.origin.name.isNotEmpty()) {
                    char.origin.name
                } else {
                    "Unknown"
                }

                binding.tvLocation.text = if (char.location.name.isNotEmpty()) {
                    char.location.name
                } else {
                    "Unknown"
                }

                // Set episode count
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

    // Handles toolbar navigation button clicks
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