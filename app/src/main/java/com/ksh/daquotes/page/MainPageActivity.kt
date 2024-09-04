package com.ksh.daquotes.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ksh.daquotes.R
import com.ksh.daquotes.databinding.ActivityMainpageBinding
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

class MainPageActivity:AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainpageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("확인", "넘어감 현재 MainPage")
        binding = ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuotes()

        CoroutineScope(Dispatchers.IO).launch {
            var result = db.quoteDao().getSearch(binding.quoteText.text.toString())
            if(result != null) {
                withContext(Dispatchers.Main) {
                    binding.likeBtn.setImageResource(R.drawable.red_like_24)
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.likeBtn.setImageResource(R.drawable.like_24)
                }
            }
        }

        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "daily Quote\n\n${binding.quoteText.text}\n${binding.author.text}")

            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }

        binding.likeBtn.setOnClickListener {
            // 모든 필드를 올바르게 전달하여 객체 생성
            var quote = Quote(message = binding.quoteText.text.toString(), author = binding.author.text.toString())
            CoroutineScope(Dispatchers.IO).launch {
                var result = db.quoteDao().getSearch(binding.quoteText.text.toString())
                if(result != null) {
                    db.quoteDao().delete(quote.message)
                    Log.d("확인용", "결과가 있습니다. 그래서 DB에 제거합니다")
                    withContext(Dispatchers.Main) {
                        binding.likeBtn.setImageResource(R.drawable.like_24)
                    }
                } else {
                    db.quoteDao().insert(quote)
                    Log.d("확인용", "결과가 없습니다. 그래서 DB에 추가합니다")
                    withContext(Dispatchers.Main) {
                        binding.likeBtn.setImageResource(R.drawable.red_like_24)
                    }
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("사이드바 테스트", "onNavigationItemSelected: ${item.title}")
        binding.layoutDraw.closeDrawers()
        return true
    }

    // 사이드바 메뉴 열고 닫고 하는 함수
    private fun toggleDrawerLayout(drawerLayout: DrawerLayout) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START)
        } else {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    //명언 가져오는 함수
    private fun getQuotes() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://korean-advice-open-api.vercel.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(api::class.java)

        api.getQuote().enqueue(object : Callback<DTO> {
            override fun onResponse(call: Call<DTO>, response: Response<DTO>) {
                if (response.isSuccessful) {
                    binding.quoteText.text = response.body()?.message.toString()
                    binding.author.text = "- ${response.body()?.author.toString()} -"
                }
            }

            override fun onFailure(call: Call<DTO>, t: Throwable) {
                Log.d("확인용", t.message.toString())
            }
        })
    }
}
