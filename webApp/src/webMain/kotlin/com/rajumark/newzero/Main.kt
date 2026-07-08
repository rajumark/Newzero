package com.rajumark.newzero

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.core.FeedManager
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val appModule = module {
    single { FeedManager(get(), get(), AppSettings(setOf("https://blog.jetbrains.com/kotlin/feed/", "https://androidweekly.net/rss.xml"))) }
    single<FeedCache> {
        FeedCache(
            Settings(),
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
        )
    }
    single { ArticleStore(get()) }
    single { RssService(get()) }
    single { HttpClient(false) }
}

private fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport {
        RssReaderApp()
    }
}
