package com.example.glance

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.OnConflictStrategy
import kotlinx.coroutines.flow.Flow
@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<Item>): List<Long>


    @Query("SELECT * FROM Item ORDER BY publishedDate DESC")
    fun observeItems(): Flow<List<Item>>

    @Query("DELETE FROM Item WHERE publishedDate < :cutoff")
    suspend fun deleteItemsOlderThan(cutoff: Long): Int

}