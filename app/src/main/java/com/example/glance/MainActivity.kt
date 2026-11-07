package com.example.glance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.glance.ui.theme.GlanceTheme
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val sDao = db.sourceDao()
        val iDao = db.itemDao()

        setContent {
            GlanceTheme {
                //recollects automatically whenever the Item table changes
                val items by iDao.observeItems().collectAsState(initial = emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DisplayTitles(items, modifier = Modifier.padding(innerPadding))
                }
            }
        }

        //fetch and store. The list above updates itself as rows land.
        lifecycleScope.launch {
            val bbcUrl = "https://feeds.bbci.co.uk/news/rss.xml"

            //returns -1 if this url already exists, so look the id up separately
            sDao.insertSource(Source(url = bbcUrl, name = "bbc", dateAdded = System.currentTimeMillis()))
            val sourceId = sDao.getSourceIdByUrl(bbcUrl) ?: return@launch

            val rssChannel = RssParser().getRssChannel(bbcUrl)

            val items = rssChannel.items.mapNotNull { ItemMapper(it, sourceId) }
            val results = iDao.insertItems(items)
            val skipped = results.count { it == -1L }
            Log.d("DB", "Fetched ${rssChannel.items.size}, mapped ${items.size}, inserted ${items.size - skipped}, skipped $skipped duplicates")
        }
    }
}

@Composable
fun DisplayTitles(itemList: List<Item>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(itemList, key = { it.id }) { item ->
            Text(text = item.title)
        }
    }
}