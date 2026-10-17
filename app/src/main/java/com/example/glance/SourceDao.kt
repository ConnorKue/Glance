package com.example.glance
import androidx.room3.Dao
import androidx.room3.Insert


@Dao
interface SourceDao {

    @Insert
    suspend fun insertSource(source: Source): Long


}