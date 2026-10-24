package com.example.glance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.room3.PrimaryKey
import com.example.glance.ui.theme.GlanceTheme
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    val stateHolder = mutableStateOf(listOf<String>())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlanceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DisplayTitles(stateHolder.value, modifier = Modifier.padding(innerPadding))
                }
            }
        }

        //start a coroutine to get the information from a hard-coded RSS URL
        lifecycleScope.launch {
            //database instance
            val db = AppDatabase.getInstance(this@MainActivity)

            //sources DAO
            val sDao = db.sourceDao()

            //hardcoded source insert to test DB
            val sourceid = sDao.insertSource(Source(url = "https://feeds.bbci.co.uk/news/rss.xml", name = "bbc", dateAdded = System.currentTimeMillis()))

            //items DAO
            val iDal = db.itemDao()

            //hardcoded item insert to test DB
//            val itemid = iDal.insertItem(Item(sourceId = sourceid, title = "testTitle", link = "fakeurl.sup", content = "testContent", publishedDate = System.currentTimeMillis(), guid = "testguid"))







            //makes the parser object
            val rssParser: RssParser = RssParser()

            //makes the channel object (for a specific source) has a .items field
            val rssChannel: RssChannel = rssParser.getRssChannel("https://feeds.bbci.co.uk/news/rss.xml")








            //source and item have been inserted
            //query all sources and items and log sizes of both lists

            //query sources and log size
//            val currentsourceTable = sDao.querySources()
//            Log.d("DB", "Sources after insert: " + currentsourceTable.size)
            //query items and log size
//            val currentitemsTable = iDal.queryItems()
//            Log.d("DB", "Items after insert: " + currentitemsTable.size)

            //delete the source least recently inserted
//            sDao.deleteSource(currentsourceTable.first())

            //delete the source most recently inserted
            //sDao.deleteSource(currentsourceTable.last())


            //query both tables again and log sizes again

            //query sources and log size
//            val newSourceTable = sDao.querySources()
//            Log.d("DB", "Sources after delete: " + newSourceTable.size)
            //query items and log size
//            val newItemsTable = iDal.queryItems()
//            Log.d("DB", "Items after delete: " + newItemsTable.size)



            //arguments
            //from hardcoded BBC source
            val currentArticle = rssChannel.items.first()
            val sourceID = sourceid


            val dbItem = ItemMapper(currentArticle, sourceID)

            Log.d("RSS", dbItem.toString())







            //direct parsing bs




            val channelItemTitles = mutableListOf<String?>()

            for (article in rssChannel.items) {

                channelItemTitles.add(article.title)

//                Log.d("RSS", article.pubDate ?: "No pubDate")
            }

            val validChannelItemTitles = mutableListOf<String>()

            validChannelItemTitles.addAll(channelItemTitles.filterNotNull())

            stateHolder.value = validChannelItemTitles

        }






    }
}

@Composable
fun DisplayTitles(itemList: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(itemList) { item ->
            Text(
                text = item,
            )
        }
    }

}
