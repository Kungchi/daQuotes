package com.ksh.daquotes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.ksh.daquotes.page.MainPage.MainPageActivity
import com.ksh.daquotes.utility.AppDatabase

lateinit var db:AppDatabase
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //db 초기화
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "quotes-db"
        ).build()

        val intent = Intent(this, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}