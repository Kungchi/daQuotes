package com.ksh.daquotes.page

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ksh.daquotes.databinding.ActivityMainpageBinding
import com.ksh.daquotes.utility.DTO
import com.ksh.daquotes.utility.api
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

        binding.toolbar.ibToolbar.setOnClickListener {
            toggleDrawerLayout(binding.root)
        }
        binding.navView.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("사이드바 테스트", "onNavigationItemSelected: ${item.title}")
        binding.layoutDraw.closeDrawers()
        return true
    }

    // 사이드바 메뉴 열고 닫고 하는 함수
    private fun toggleDrawerLayout(drawerLayout: DrawerLayout) {
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        else {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun getQuotes() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.quotable.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(api::class.java)

        api.getQuote().enqueue(object : Callback<List<DTO>> {
            override fun onResponse(call: Call<List<DTO>>, response: Response<List<DTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let { quotes ->
                        for(quote in quotes) {
                            binding.quoteText.text = quote.content
                            binding.author.text = quote.author
                        }
                    }

                }
            }

            override fun onFailure(call: Call<List<DTO>>, t: Throwable) {
                Log.d("확인용", t.message.toString())
            }
        })
    }
}
