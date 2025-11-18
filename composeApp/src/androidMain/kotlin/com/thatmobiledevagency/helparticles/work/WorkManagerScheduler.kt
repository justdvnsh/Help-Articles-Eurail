package com.thatmobiledevagency.helparticles.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules background refresh work using WorkManager.
 *
 * Scheduling Strategy:
 * - Periodic work every 24 hours with 12-hour flex interval
 * - Only runs when device is connected to network
 * - Only runs when battery is not low
 * - Uses KEEP policy to avoid duplicate work requests
 *
 * Rationale:
 * - 24-hour period ensures cache is refreshed roughly once per day
 * - 12-hour flex allows WorkManager to optimize execution within a 12-24 hour window
 * - Network constraint ensures we don't waste battery on failed requests
 * - Battery constraint respects user's device battery state
 */
class WorkManagerScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME = "refresh_articles_work"
        private const val REPEAT_INTERVAL_HOURS = 24L
        private const val FLEX_INTERVAL_HOURS = 12L
    }

    fun schedulePeriodicRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val refreshWork = PeriodicWorkRequestBuilder<RefreshArticlesWorker>(
            REPEAT_INTERVAL_HOURS, TimeUnit.HOURS,
            FLEX_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshWork
        )
    }

    fun cancelPeriodicRefresh() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}