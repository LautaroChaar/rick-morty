package com.example.rickmorty.ui.main

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickmorty.R
import com.example.rickmorty.data.model.Character
import com.example.rickmorty.databinding.ItemCharacterBinding

// RecyclerView adapter for displaying character list
class CharacterAdapter(
    private val onItemClick: (Character) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    private var characters = listOf<Character>()

    // ViewHolder class for character items
    inner class CharacterViewHolder(
        private val binding: ItemCharacterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        //Binds character data to views
        fun bind(character: Character) {
            with(binding) {
                // Set character name
                tvCharacterName.text = character.name

                // Set species and status
                tvCharacterSpecies.text = itemView.context.getString(R.string.species, character.species)
                tvCharacterStatus.text = character.status

                // Set status indicator color based on status
                val statusColor = when (character.status.lowercase()) {
                    "alive" -> R.color.status_alive
                    "dead" -> R.color.status_dead
                    else -> R.color.status_unknown
                }

                // Create circular drawable
                val circleDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(ContextCompat.getColor(root.context, statusColor))
                }
                viewStatusIndicator.background = circleDrawable

                // Set location
                tvCharacterLocation.text = character.location.name

                // Load character image with Glide
                Glide.with(root.context)
                    .load(character.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.placeholder_character)
                    .error(R.drawable.placeholder_character)
                    .into(ivCharacterImage)

                // Set click listener
                root.setOnClickListener {
                    onItemClick(character)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount(): Int = characters.size

    // Updates the character list using DiffUtil for efficient updates
    fun updateCharacters(newCharacters: List<Character>) {
        val diffCallback = CharacterDiffCallback(characters, newCharacters)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        characters = newCharacters
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil callback for efficient list updates
    private class CharacterDiffCallback(
        private val oldList: List<Character>,
        private val newList: List<Character>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}