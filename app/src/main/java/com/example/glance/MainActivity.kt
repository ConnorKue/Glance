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

import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val sDao = db.sourceDao()
        val iDao = db.itemDao()
        val refreshPrefs = RefreshPrefs(this)

        setContent {
            GlanceTheme {
                //recollects automatically whenever the Item table changes
                val items by iDao.observeItems().collectAsState(initial = emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DisplayTitles(items, modifier = Modifier.padding(innerPadding))
                }
            }
        }

        lifecycleScope.launch {
            Log.d("Refresh", "Last refreshed: ${refreshPrefs.getLastRefreshed()} (0 = never)")

            //temp test of shouldRefresh
            val testNow = 10_000L
            Log.d("RefreshTest", "never refreshed (expect true): ${shouldRefresh(0L, testNow)}")
            Log.d("RefreshTest", "1 min ago (expect false): ${shouldRefresh(testNow - 60, testNow)}")
            Log.d("RefreshTest", "14m59s ago (expect false): ${shouldRefresh(testNow - 899, testNow)}")
            Log.d("RefreshTest", "exactly 15 min ago (expect true): ${shouldRefresh(testNow - 900, testNow)}")
            Log.d("RefreshTest", "1 hour ago (expect true): ${shouldRefresh(testNow - 3600, testNow)}")
            Log.d("RefreshTest", "in the future (expect true): ${shouldRefresh(testNow + 500, testNow)}")
            Log.d("RefreshTest", "real stored value: ${shouldRefresh(refreshPrefs.getLastRefreshed(), Instant.now().epochSecond)}")



            val bbcUrl = "https://feeds.bbci.co.uk/news/rss.xml"

            //Anything published before this is too old to keep
            val cutoff = Instant.now().minus(30, ChronoUnit.DAYS).epochSecond

            //Returns -1 if this url already exists
            sDao.insertSource(Source(url = bbcUrl, name = "bbc", dateAdded = System.currentTimeMillis()))
            val sourceId = sDao.getSourceIdByUrl(bbcUrl) ?: return@launch

            try {
                val rssChannel = RssParser().getRssChannel(bbcUrl)

                val items = rssChannel.items
                    .mapNotNull { ItemMapper(it, sourceId) }
                    .filter { it.publishedDate >= cutoff }
                val results = iDao.insertItems(items)
                val skipped = results.count { it == -1L }
                Log.d("DB", "Fetched ${rssChannel.items.size}, inserted ${items.size - skipped}, skipped $skipped duplicates")

                //only reached if the fetch succeeded, so a failed fetch doesn't reset the clock
                refreshPrefs.setLastRefreshed(Instant.now().epochSecond)
            } catch (e: Exception) {
                Log.w("RSS", "Fetch failed for $bbcUrl", e)
            }

            //outside the try/catch
            val deleted = iDao.deleteItemsOlderThan(cutoff)
            Log.d("DB", "Deleted $deleted items older than 30 days")
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