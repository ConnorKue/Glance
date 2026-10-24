package com.example.glance
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query


@Dao
interface SourceDao {

    @Insert
    suspend fun insertSource(source: Source): Long

    @Query("SELECT * FROM Source")
    suspend fun querySources(): List<Source>

    @Delete
    suspend fun deleteSource(source: Source): Int

}