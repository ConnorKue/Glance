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
    val id: Long = 0,
    val sourceId: Long,

    //necessary, cant be nullable
    val guid: String,
    val title: String,
    val author: String,
    val link: String?,
    val publishedDate: Long,
    val description: String?,
    //skipping content
    val image: String?,
    //skipping audio
    //skipping video
    //skipping sourceName
    //skipping sourceURL
    //skipping categories
    //skipping itunes
    val commentsUrl: String?
    //skipping youtube






)


