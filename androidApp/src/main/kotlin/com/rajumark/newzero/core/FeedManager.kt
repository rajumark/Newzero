package com.rajumark.newzero.core

import android.content.Context
import com.rajumark.newzero.datasource.network.RssService
import com.rajumark.newzero.datasource.storage.FeedCache
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

fun buildFeedManager(ctx: Context, withLog: Boolean) = FeedManager(
    RssService(
        HttpClient(withLog)
    ),
    FeedCache(
        SharedPreferencesSettings(
            ctx.getSharedPreferences(
                "rss_reader_pref",
                Context.MODE_PRIVATE
            )
        ),
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    )
).also {
    if (withLog) Napier.base(DebugAntilog())
}