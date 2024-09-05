package com.ksh.daquotes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.ksh.daquotes.databinding.ActivityMainpageBinding
import com.ksh.daquotes.page.FavoritesPage.FavoritesFragment
import com.ksh.daquotes.page.MainPage.MainPageFragment

class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainpageBinding
    private val adRequest = AdRequest.Builder().build()
    //전면 광고 초기화 되었는지
    private var ads: InterstitialAd? = null

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (ads != null) {
                ads?.show(this@MainPageActivity)
                ads?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        finish()
                    }
                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        finish()
                    }
                }
            } else {
                this.isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                finish()
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


        // 프래그먼트를 로드
        loadFragment(MainPageFragment())

        MobileAds.initialize(this)
        binding.adView.loadAd(adRequest)
        startAd()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentFragment = this.supportFragmentManager.findFragmentById(R.id.fragment_container)

        when (item.itemId) {
            R.id.daily_quote -> {
                if (currentFragment !is MainPageFragment) {
                    loadFragment(MainPageFragment())
                }
            }
            R.id.favorite_quote -> {
                if (currentFragment !is FavoritesFragment) {
                    loadFragment(FavoritesFragment())
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
