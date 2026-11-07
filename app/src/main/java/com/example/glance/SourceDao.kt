package com.example.glance
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.OnConflictStrategy

@Dao
interface SourceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSource(source: Source): Long


    @Query("SELECT id FROM Source WHERE url = :url")
    suspend fun getSourceIdByUrl(url: String): Long?

    @Delete
    suspend fun deleteSource(source: Source): Int

}