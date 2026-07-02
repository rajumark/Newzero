package com.rajumark.newzero.sync

import android.content.Context
import androidx.work.*
import com.rajumark.newzero.core.FeedManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val rssReader: FeedManager by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.Main) {
        rssReader.loadAllSources(true)
        Result.success()
    }

    companion object {
        private const val WORK_NAME = "refresh_work_name"
        fun schedule(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS).build()
            )
        }
    }
}