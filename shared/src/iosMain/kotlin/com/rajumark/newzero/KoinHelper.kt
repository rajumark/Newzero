package com.rajumark.newzero

import com.rajumark.newzero.app.FeedStore
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.core.RssReader
import com.rajumark.newzero.datasource.network.FeedLoader
import com.rajumark.newzero.datasource.storage.FeedStorage
import com.russhwolf.settings.NSUserDefaultsSettings
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private val appModule = module {
    single { RssReader(get(), get(), Settings(setOf("https://example.com/feed.xml"))) }
    single<FeedStorage> {
        FeedStorage(
            NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults()),
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

class KoinHelper : KoinComponent {
    val rssReader by inject<RssReader>()
    val feedStore by inject<FeedStore>()
}

fun initKoin() {
    startKoin { modules(appModule) }
}