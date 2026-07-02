package com.rajumark.newzero

import android.app.Application
import com.rajumark.newzero.app.ArticleStore
import com.rajumark.newzero.core.FeedManager
import com.rajumark.newzero.core.HttpClient
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.rajumark.newzero.sync.SyncWorker
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        launchBackgroundSync()
    }

    private val appModule = module {
        single { FeedManager(get(), get(), AppSettings(setOf("https://blog.jetbrains.com/kotlin/feed/"))) }
        single {
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
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun launchBackgroundSync() {
        SyncWorker.schedule(this)
    }
}
