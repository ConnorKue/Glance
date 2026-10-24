package com.example.glance

import android.util.Log
import com.prof18.rssparser.model.RssItem
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

//convert one parsed RSS article into an item
//if article cannot be converted, return null

fun ItemMapper(currentArticle: RssItem, sourceID: Long) : Item?{


    //name = guid : type = String : type from parser = String? : notes = required.
    val parsedGuid: String? = currentArticle.guid
    if (parsedGuid == null){
        return null
    }

    val itemGuid: String = parsedGuid

    //name = title : type = String : type from parser = String? : notes = fallback to "Untitled"
    val itemTitle = currentArticle.title ?: "Untitled"

    //name = author : type = String : type from parser = String? : notes = fallback to "Not listed"
    val itemAuthor = currentArticle.author ?: "Not listed"

    //name = link : type = String? : type from parser = String? : notes = NA
    val itemLink = currentArticle.link

    //name = publishedDate : type = Long : type from parser = String? : notes =
    //RFC 1123 date and time specification from the parser, must be converted to Unix time
    //fallback to current time
    val parsedDate = currentArticle.pubDate
    //if its null, then default to current time, otherwise try converting
    val itemPublishedDate: Long = if(parsedDate == null) {
        Instant.now().epochSecond
    }
    else {
        try {
            ZonedDateTime.parse(parsedDate.trim(), DateTimeFormatter.RFC_1123_DATE_TIME).toEpochSecond()
        } catch (e: Exception) {
            Log.w("ItemMapper", "could not parse item publishedDate, defaulting to current time: ", e)
            Instant.now().epochSecond
        }
    }

    //name = description : type = String : type from parser = String? : notes = default to "No description"
    val itemDescription = currentArticle.description ?: "No description"

    //name = image : type = String? : type from parser = String? : notes = NA
    val itemImage = currentArticle.image

    //name = commentsUrl : type = String? : type from parser = String? : notes = NA
    val itemCommentsUrl = currentArticle.commentsUrl


    return Item (
        sourceId = sourceID,
        guid = itemGuid,
        title = itemTitle,
        author = itemAuthor,
        link = itemLink,
        publishedDate = itemPublishedDate,
        description =  itemDescription,
        image =  itemImage,
        commentsUrl =  itemCommentsUrl
    )
}
