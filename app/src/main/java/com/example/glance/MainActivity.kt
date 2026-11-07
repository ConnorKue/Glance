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
            val sDao = db.sourceDao()
            val iDao = db.itemDao()

            val bbcUrl = "https://feeds.bbci.co.uk/news/rss.xml"

            //returns -1 if this url already exists, so look the id up separately
            sDao.insertSource(Source(url = bbcUrl, name = "bbc", dateAdded = System.currentTimeMillis()))
            val sourceId = sDao.getSourceIdByUrl(bbcUrl) ?: return@launch

            val rssParser = RssParser()
            val rssChannel = rssParser.getRssChannel(bbcUrl)

            //map every article, dropping the ones the mapper rejected (returned null)
            val items = rssChannel.items.mapNotNull { ItemMapper(it, sourceId) }
            val results = iDao.insertItems(items)
            val skipped = results.count { it == -1L }
            Log.d("DB", "Fetched ${rssChannel.items.size}, mapped ${items.size}, inserted ${items.size - skipped}, skipped $skipped duplicates")







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
