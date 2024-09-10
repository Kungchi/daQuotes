package com.ksh.daquotes.page.FavoritesPage

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksh.daquotes.R
import com.ksh.daquotes.databinding.ItemFavoritesBinding
import com.ksh.daquotes.page.DetailPage.DetailActivity
import com.ksh.daquotes.utility.Quote

class FavoritesAdapter(private val quote: List<Quote>) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    class FavoritesViewHolder(val binding: ItemFavoritesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemFavoritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.binding.quoteTextFavorite.text = quote[position].message
        holder.binding.authorTextFavorite.text = quote[position].author

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("QUOTE_MESSAGE", quote[position].message)
                putExtra("QUOTE_AUTHOR", quote[position].author)
            }
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (context is Activity) {
                context.startActivity(intent)
                context.overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    override fun getItemCount(): Int {
        return quote.size
    }
}

