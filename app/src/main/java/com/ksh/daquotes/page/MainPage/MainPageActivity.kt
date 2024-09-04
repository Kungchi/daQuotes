package com.ksh.daquotes.page.MainPage

import MainPageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
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
    private lateinit var mainPageAdapter: MainPageAdapter
    private var currentQuote: Quote? = null // 현재 페이지의 명언을 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("확인", "넘어감 현재 MainPage")
        binding = ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainPageAdapter = MainPageAdapter(mutableListOf())
        binding.viewPager.adapter = mainPageAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        getQuotes()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentQuote = mainPageAdapter.getQuote(position)

                CoroutineScope(Dispatchers.IO).launch {
                    var result = db.quoteDao().getSearch(currentQuote?.message)
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

                if (position == mainPageAdapter.itemCount - 1) {
                    getQuotes()
                }
            }
        })

        MobileAds.initialize(this)
        val ads = AdRequest.Builder().build()
        binding.adView.loadAd(ads)

        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "daily Quote\n\n${currentQuote?.message}\n- ${currentQuote?.author} -")

            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }

        binding.likeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                var result = db.quoteDao().getSearch(currentQuote?.message)
                if(result != null) {
                    db.quoteDao().delete(currentQuote?.message)
                    Log.d("확인용", "결과가 있습니다. 그래서 DB에 제거합니다")
                    withContext(Dispatchers.Main) {
                        binding.likeBtn.setImageResource(R.drawable.like_24)
                    }
                } else {
                    db.quoteDao().insert(currentQuote!!)
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
}
