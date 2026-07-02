package com.rajumark.newzero

import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.core.FeedManager
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.russhwolf.settings.NSUserDefaultsSettings
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private val appModule = module {
    single { FeedManager(get(), get(), AppSettings(setOf("https://example.com/feed.xml"))) }
    single<FeedCache> {
        FeedCache(
            NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults()),
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

class DIHelper : KoinComponent {
    val rssReader by inject<FeedManager>()
    val feedStore by inject<ArticleStore>()
}

fun initKoin() {
    startKoin { modules(appModule) }
}