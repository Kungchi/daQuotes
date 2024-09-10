package com.ksh.daquotes.page.DetailPage

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ksh.daquotes.R
import com.ksh.daquotes.databinding.ActivityDetailBinding
import com.ksh.daquotes.page.FavoritesPage.FavoritesViewModel
import com.ksh.daquotes.utility.Quote

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private val viewModel: FavoritesViewModel by viewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        var quoteText = intent.getStringExtra("QUOTE_MESSAGE")
        var quoteAuthor = intent.getStringExtra("QUOTE_AUTHOR")
        val currentQuote = Quote(message = quoteText, author = quoteAuthor)

        viewModel.liveDate.observe(this) { quotes ->
            val isFavorite = quotes.any { it.message == currentQuote.message }
            updateImg(isFavorite)
        }

        binding.likeBtn.setOnClickListener {
            viewModel.add_remove(currentQuote)
        }

        binding.quoteText.text = quoteText
        binding.authorText.text = quoteAuthor
    }

    private fun updateImg(isFavorite: Boolean) {
        // 즐겨찾기 상태에 따라 버튼 아이콘 업데이트
        binding.likeBtn.setImageResource(
            if (isFavorite) R.drawable.red_like_icon else R.drawable.like_icon
        )
    }
}