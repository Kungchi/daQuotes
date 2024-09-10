package com.ksh.daquotes

import MainPageAdapter
import android.content.Intent
import android.os.Bundle
import android.provider.LiveFolders.INTENT
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.ksh.daquotes.databinding.ActivityMainpageBinding
import com.ksh.daquotes.page.FavoritesPage.FavoritesViewModel
import com.ksh.daquotes.utility.DTO
import com.ksh.daquotes.utility.Quote
import com.ksh.daquotes.utility.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainpageBinding
    private lateinit var mainPageAdapter: MainPageAdapter
    private val adRequest = AdRequest.Builder().build()
    private var currentQuote: Quote? = null
    //전면광고 나오는지 안나오는지 여부 확인하는 변수
    private var ads: InterstitialAd? = null
    private val viewModel: FavoritesViewModel by viewModels()

    //뒤로가기 버튼
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (ads != null) {
                // 전면 광고가 있을 경우 광고를 먼저 표시
                ads?.show(this@MainPageActivity)
                ads?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // 광고가 닫힌 후 앱 종료
                        finishAffinity() // 안전하게 앱 종료
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        // 광고 표시가 실패하면 앱 종료
                        finishAffinity() // 안전하게 앱 종료
                    }
                }
            } else {
                // 광고가 없을 경우 바로 앱 종료
                this.isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                finishAffinity() // 안전하게 앱 종료
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


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

        //광고 활성화
        MobileAds.initialize(this)
        binding.adView.loadAd(adRequest)
        //전면광고 활성화
        startAd()
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

    private fun startAd() {
        InterstitialAd.load(this, getString(R.string.ad_testinterstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                ads = ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                ads = null
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
