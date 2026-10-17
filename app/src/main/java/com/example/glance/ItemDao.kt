package com.example.glance

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query


@Dao
interface ItemDao {

    @Insert
    suspend fun insertItem(item:Item): Long


    @Query("SELECT * FROM Item")
    suspend fun queryItems(): List<Item>


}