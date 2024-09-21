package com.ksh.daquote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.ksh.daquote.databinding.ActivityFavoritesBinding
import com.ksh.daquote.page.FavoritesPage.FavoritesAdapter
import com.ksh.daquote.page.FavoritesPage.FavoritesViewModel

class FavoritesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    private val viewModel: FavoritesViewModel by viewModels()
    private val adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 사이드바 및 버튼 설정
        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

        viewModel.liveDate.observe(this) { quotes ->
            Log.d("확인용", quotes.size.toString())
            adapter = FavoritesAdapter(quotes)
            binding.favoritesRecyclerView.layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            binding.favoritesRecyclerView.adapter = adapter
        }

        MobileAds.initialize(this)
        binding.adView.loadAd(adRequest)
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

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
