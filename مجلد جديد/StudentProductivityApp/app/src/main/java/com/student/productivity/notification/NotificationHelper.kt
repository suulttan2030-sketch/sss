package com.student.productivity.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.student.productivity.R

object NotificationHelper {

    const val DAILY_REMINDERS_CHANNEL_ID = "daily_reminders_channel"
    const val PORTAL_CHECK_CHANNEL_ID = "portal_check_channel"

    private const val DAILY_REMINDERS_NOTIFICATION_ID = 1001
    private const val PORTAL_CHECK_NOTIFICATION_ID = 1002

    /**
     * Creates the notification channels required for the app.
     * Call this in Application onCreate() or before showing notifications.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nameDaily = "Daily Study & Tasks"
            val descDaily = "Daily reminders for chapters, assignments, and exams."
            val importanceDaily = NotificationManager.IMPORTANCE_DEFAULT
            val channelDaily = NotificationChannel(DAILY_REMINDERS_CHANNEL_ID, nameDaily, importanceDaily).apply {
                description = descDaily
            }

            val namePortal = "University Updates"
            val descPortal = "Reminders to check the university portal for updates."
            val importancePortal = NotificationManager.IMPORTANCE_DEFAULT
            val channelPortal = NotificationChannel(PORTAL_CHECK_CHANNEL_ID, namePortal, importancePortal).apply {
                description = descPortal
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelDaily)
            notificationManager.createNotificationChannel(channelPortal)
        }
    }

    /**
     * Sends the daily reminder notification.
     */
    fun sendDailyReminder(
        context: Context,
        chapterDetails: String,
        taskDetails: String,
        proximityDetails: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Construct notification style and content
        val inboxStyle = NotificationCompat.InboxStyle()
        
        var hasContent = false
        if (chapterDetails.isNotEmpty()) {
            inboxStyle.addLine("Chapters: $chapterDetails")
            hasContent = true
        }
        if (taskDetails.isNotEmpty()) {
            inboxStyle.addLine("Pending Tasks: $taskDetails")
            hasContent = true
        }
        if (proximityDetails.isNotEmpty()) {
            inboxStyle.addLine("Proximity Warning: $proximityDetails")
            hasContent = true
        }

        if (!hasContent) {
            // Nothing to remind today
            return
        }

        // We use android.R.drawable.ic_dialog_info as a fallback icon to avoid resource missing errors
        val builder = NotificationCompat.Builder(context, DAILY_REMINDERS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Study & Deadline Update")
            .setContentText("Tap to see your tasks and active chapters for today.")
            .setStyle(inboxStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(DAILY_REMINDERS_NOTIFICATION_ID, builder.build())
    }

    /**
     * Sends the portal check notification.
     */
    fun sendPortalCheckReminder(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, PORTAL_CHECK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("University Portal Check")
            .setContentText("It's time to check your university portal for updates, grades, or announcements.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(PORTAL_CHECK_NOTIFICATION_ID, builder.build())
    }
}
