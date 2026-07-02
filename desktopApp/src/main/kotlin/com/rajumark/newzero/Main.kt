package com.rajumark.newzero

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.core.FeedManager
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.russhwolf.settings.PropertiesSettings
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.*

private val appModule = module {
single { FeedManager(get(), get(), AppSettings(setOf("https://blog.jetbrains.com/kotlin/feed/"))) }
    single<FeedCache> {
        FeedCache(
            PropertiesSettings(Properties()),
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

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Newzero",
    ) {
        RssReaderApp()
    }
}