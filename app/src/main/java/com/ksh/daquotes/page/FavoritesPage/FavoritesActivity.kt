package com.ksh.daquotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.ksh.daquotes.databinding.ActivityFavoritesBinding
import com.ksh.daquotes.page.FavoritesPage.FavoritesAdapter
import com.ksh.daquotes.utility.Quote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    private val adRequest = AdRequest.Builder().build()
    //전면 광고 초기화 되었는지
    private var ads: InterstitialAd? = null

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (ads != null) {
                // 전면 광고가 있을 경우 광고를 먼저 표시
                ads?.show(this@FavoritesActivity)
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
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


        // 사이드바 및 버튼 설정
        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        CoroutineScope(Dispatchers.IO).launch {
            var quote_list: List<Quote> = db.quoteDao().getAll()
            Log.e("테스트", "db 계속 조회중")
            withContext(Dispatchers.Main) {
                adapter = FavoritesAdapter(quote_list)
                binding.favoritesRecyclerView.layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
                binding.favoritesRecyclerView.adapter = adapter
            }
        }

        MobileAds.initialize(this)
        binding.adView.loadAd(adRequest)
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
}
