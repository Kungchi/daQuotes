package com.ksh.daquotes.utility

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 기본값 0
    @ColumnInfo(name = "message") val message: String?,
    @ColumnInfo(name = "author") val author: String?
)

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

@Database(entities = [Quote::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}