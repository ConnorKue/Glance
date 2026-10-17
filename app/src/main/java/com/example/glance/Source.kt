package com.example.glance

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity
data class Source(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String?,
    val name: String?,
    val dateAdded: Long


)
