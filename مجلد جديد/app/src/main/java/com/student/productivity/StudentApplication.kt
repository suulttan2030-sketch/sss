package com.student.productivity

import android.app.Application
import com.student.productivity.notification.NotificationHelper
import com.student.productivity.worker.WorkScheduler

class StudentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create the notification channels
        NotificationHelper.createNotificationChannels(this)

        // Schedule background reminders
        WorkScheduler.scheduleDailyReminder(this)
        WorkScheduler.schedulePortalCheck(this)
    }
}
