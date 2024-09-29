package com.ksh.daquote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.ksh.daquote.utility.AppDatabase

//db를 접근할수있게 전역변수로 설정
lateinit var db:AppDatabase
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //db 초기화
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "quotes-db"
        ).build()
        //메인 페이지 액티비티 시작
        val intent = Intent(this, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
