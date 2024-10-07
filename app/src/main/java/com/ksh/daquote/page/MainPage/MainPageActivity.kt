package com.ksh.daquote

import MainPageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.ksh.daquote.databinding.ActivityMainpageBinding
import com.ksh.daquote.page.FavoritesPage.FavoritesViewModel
import com.ksh.daquote.utility.DTO
import com.ksh.daquote.utility.Quote
import com.ksh.daquote.utility.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainpageBinding
    private lateinit var mainPageAdapter: MainPageAdapter
    private var currentQuote: Quote? = null
    private val viewModel: FavoritesViewModel by viewModels()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MainPageActivity) {}
        }

        // 사이드바 및 버튼 설정
        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        mainPageAdapter = MainPageAdapter(mutableListOf())
        binding.viewPager.adapter = mainPageAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL


        //명언 보여주기
        getQuotes()

        //버튼 그룹
        buttonGroup()

        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        InterstitialAd.load(this,resources.getString(R.string.ad_interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("애드몹 광고", adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("애드몹 광고", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    //메뉴버튼 작동
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.daily_quote -> {
                if (javaClass != MainPageActivity::class.java) {
                    val intent = Intent(this, MainPageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
            R.id.favorite_quote -> {
                if (javaClass != FavoritesActivity::class.java) {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(this)
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                }
            }
            R.id.quote_challenge -> {
                Toast.makeText(applicationContext, "아직 미구현", Toast.LENGTH_SHORT).show()
            }
        }

        // Drawer 닫기
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

    //버튼들 모음 및 뷰페이저 콜백
    private fun buttonGroup() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentQuote = mainPageAdapter.getQuote(position)
                viewModel.liveDate.observe(this@MainPageActivity) { quotes ->
                    val isFavorite = quotes.any { it.message == currentQuote!!.message }
                    updateImg(isFavorite)
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
            viewModel.add_remove(currentQuote!!)
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

    private fun updateImg(isFavorite: Boolean) {
        binding.likeBtn.setImageResource(
            if (isFavorite) R.drawable.red_like_icon else R.drawable.like_icon
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
