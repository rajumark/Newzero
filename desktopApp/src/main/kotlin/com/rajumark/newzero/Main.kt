package com.rajumark.newzero

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rajumark.newzero.app.FeedStore
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.core.RssReader
import com.rajumark.newzero.datasource.network.FeedLoader
import com.rajumark.newzero.datasource.storage.FeedStorage
import com.russhwolf.settings.PropertiesSettings
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.*

private val appModule = module {
single { RssReader(get(), get(), Settings(setOf("https://example.com/feed.xml"))) }
    single&lt;FeedStorage&gt; {
        FeedStorage(
            PropertiesSettings(Properties()),
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
        )
    }
    single { FeedStore(get()) }
    single { FeedLoader(get()) }
    single { HttpClient(false) }
}

private fun initKoin() {
    startKoin {
        modules(appModule)
    }
}

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Newzero",
    ) {
        RssReaderApp()
    }
}