package com.ksh.daquote.utility

import androidx.room.*

//로컬DB에 저장하기위한 data class 생성
@Entity
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 기본값 0
    @ColumnInfo(name = "message") val message: String?,
    @ColumnInfo(name = "author") val author: String?
)

//쿼리를 사용하기위한 interface 선언
@Dao
interface QuoteDao {
    @Query("SELECT * FROM Quote")
    fun getAll(): List<Quote>
    @Query("SELECT * FROM Quote WHERE message = :message")
    fun getSearch(message: String?): Quote
    @Query("DELETE FROM Quote WHERE message = :message")
    fun delete(message: String?)
    @Insert
    suspend fun insert(quote: Quote): Long
}

//Room Database 설정
@Database(entities = [Quote::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}