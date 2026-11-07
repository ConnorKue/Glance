package com.example.glance

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.OnConflictStrategy

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<Item>): List<Long>


    @Query("SELECT * FROM Item")
    suspend fun queryItems(): List<Item>


}