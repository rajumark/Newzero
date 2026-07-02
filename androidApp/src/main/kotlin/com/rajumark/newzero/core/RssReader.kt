package com.rajumark.newzero.core

import android.content.Context
import com.rajumark.newzero.datasource.network.FeedLoader
import com.rajumark.newzero.datasource.storage.FeedStorage
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

fun buildRssReader(ctx: Context, withLog: Boolean) = RssReader(
    FeedLoader(
        HttpClient(withLog)
    ),
    FeedStorage(
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