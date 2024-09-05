package com.ksh.daquotes.page.FavoritesPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksh.daquotes.databinding.FavoritesItemBinding
import com.ksh.daquotes.utility.Quote

class FavoritesAdapter(private val quote: List<Quote>) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    class FavoritesViewHolder(val binding: FavoritesItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = FavoritesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.binding.quoteTextFavorite.text = quote[position].message
        holder.binding.authorTextFavorite.text = quote[position].author
    }

    override fun getItemCount(): Int {
        return quote.size
    }
}

