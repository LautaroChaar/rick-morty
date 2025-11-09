package com.example.rickmorty.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickmorty.data.model.FavoriteCharacter
import com.example.rickmorty.databinding.ItemFavoriteCharacterBinding

class FavoriteAdapter(
    private val items: List<FavoriteCharacter>,
    private val onClick: (FavoriteCharacter) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemFavoriteCharacterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val character = items[position]
        holder.binding.apply {
            tvName.text = character.name
            "${character.status} - ${character.species}".also { tvStatus.text = it }
            Glide.with(ivCharacter.context)
                .load(character.image)
                .into(ivCharacter)

            root.setOnClickListener { onClick(character) }
        }
    }

    override fun getItemCount(): Int = items.size
}
