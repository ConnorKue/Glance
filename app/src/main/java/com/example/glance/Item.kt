package com.example.glance

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Source::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sourceId")]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sourceId: Long,
    val title: String?,
    val link: String?,
    val content: String?,
    val publishedDate: Long,
    val guid: String?
)


