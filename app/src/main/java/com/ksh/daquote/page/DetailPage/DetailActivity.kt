package com.ksh.daquote.page.DetailPage

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ksh.daquote.R
import com.ksh.daquote.databinding.ActivityDetailBinding
import com.ksh.daquote.page.FavoritesPage.FavoritesViewModel
import com.ksh.daquote.utility.Quote

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

        binding.quoteText.text = quoteText
        binding.authorText.text = quoteAuthor

        binding.likeBtn.setOnClickListener {
            viewModel.add_remove(currentQuote)
        }

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "daily Quote\n\n${currentQuote?.message}\n- ${currentQuote?.author} -")
            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }

    private fun updateImg(isFavorite: Boolean) {
        // 즐겨찾기 상태에 따라 버튼 아이콘 업데이트
        binding.likeBtn.setImageResource(
            if (isFavorite) R.drawable.red_like_icon else R.drawable.like_icon
        )
    }
}