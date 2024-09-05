package com.ksh.daquotes.page.MainPage

import MainPageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ksh.daquotes.R
import com.ksh.daquotes.databinding.FragmentMainpageBinding
import com.ksh.daquotes.db
import com.ksh.daquotes.utility.DTO
import com.ksh.daquotes.utility.Quote
import com.ksh.daquotes.utility.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPageFragment : Fragment() {
    private var _binding: FragmentMainpageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainPageAdapter: MainPageAdapter
    private var currentQuote: Quote? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainPageAdapter = MainPageAdapter(mutableListOf())
        binding.viewPager.adapter = mainPageAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        getQuotes()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentQuote = mainPageAdapter.getQuote(position)

                CoroutineScope(Dispatchers.IO).launch {
                    val result = db.quoteDao().getSearch(currentQuote?.message)
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            binding.likeBtn.setImageResource(R.drawable.red_like_icon)
                        } else {
                            binding.likeBtn.setImageResource(R.drawable.like_icon)
                        }
                    }
                }

                if (position == mainPageAdapter.itemCount - 1) {
                    getQuotes()
                }
            }
        })

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "daily Quote\n\n${currentQuote?.message}\n- ${currentQuote?.author} -")
            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }

        binding.likeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val result = db.quoteDao().getSearch(currentQuote?.message)
                if (result != null) {
                    db.quoteDao().delete(currentQuote?.message)
                    withContext(Dispatchers.Main) {
                        binding.likeBtn.setImageResource(R.drawable.like_icon)
                    }
                } else {
                    db.quoteDao().insert(currentQuote!!)
                    withContext(Dispatchers.Main) {
                        binding.likeBtn.setImageResource(R.drawable.red_like_icon)
                    }
                }
            }
        }
    }

    private fun getQuotes() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://korean-advice-open-api.vercel.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(api::class.java)

        api.getQuote().enqueue(object : Callback<DTO> {
            override fun onResponse(call: Call<DTO>, response: Response<DTO>) {
                if (response.isSuccessful) {
                    val quote = Quote(
                        message = response.body()?.message.toString(),
                        author = response.body()?.author.toString()
                    )
                    mainPageAdapter.addQuote(quote)
                }
            }

            override fun onFailure(call: Call<DTO>, t: Throwable) {
                Log.d("확인용", t.message.toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
