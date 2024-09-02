package com.ksh.daquotes.utility

import androidx.room.*

@Entity
data class Quote(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "author") val author: String?
)

@Dao
interface QuoteDao {
    @Query("SELECT * FROM Quote")
    fun getAll(): List<Quote>

    @Delete
    fun delete(quote: Quote)
}

@Database(entities = [Quote::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}