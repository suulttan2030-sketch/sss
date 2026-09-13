package com.student.productivity.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    const val DAILY_REMINDER_WORK_NAME = "DailyReminderWork"
    const val PORTAL_CHECK_WORK_NAME = "PortalCheckWork"

    /**
     * Schedules the daily reminder to run at exactly 12:00 PM.
     * Uses dynamic one-time scheduling to prevent the time-shift/drift bug.
     */
    fun scheduleDailyReminder(context: Context) {
        val delay = calculateDelayToTwelvePM(System.currentTimeMillis())

        val workRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingWorkPolicy.REPLACE, // REPLACE ensures the next scheduled work is fresh and accurate
            workRequest
        )
    }

    /**
     * Schedules the recurring portal check reminder to run every 3 days.
     * Uses KEEP policy to ensure we don't reset the 3-day clock on every app start.
     */
    fun schedulePortalCheck(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<PortalCheckWorker>(3, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PORTAL_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Calculates the time delay in milliseconds from [currentTimeMillis] until the next 12:00 PM.
     */
    fun calculateDelayToTwelvePM(currentTimeMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If 12:00 PM has already passed today, target 12:00 PM tomorrow
        if (calendar.timeInMillis <= currentTimeMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - currentTimeMillis
    }
}
