package com.student.productivity.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.student.productivity.data.local.AppDatabase
import com.student.productivity.data.local.entity.ChapterState
import com.student.productivity.data.repository.StudyRepository
import com.student.productivity.notification.NotificationHelper
import java.util.Calendar

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = StudyRepository(database.appDao())

            // 1. Fetch remindable chapters (In Progress & Saved)
            val remindableChapters = repository.getRemindableChapters()
            val chapterSummary = if (remindableChapters.isNotEmpty()) {
                val inProgressCount = remindableChapters.count { it.state == ChapterState.IN_PROGRESS }
                val savedCount = remindableChapters.count { it.state == ChapterState.SAVED }
                "$inProgressCount In Progress, $savedCount Saved"
            } else {
                ""
            }

            // 2. Fetch pending tasks (assignments & exams)
            val pendingTasks = repository.getPendingTasks()
            val taskSummary = if (pendingTasks.isNotEmpty()) {
                "${pendingTasks.size} pending tasks"
            } else {
                ""
            }

            // 3. Proximity alerts (due in 2-4 days)
            val currentTime = System.currentTimeMillis()
            val twoDaysMillis = 2 * 24 * 60 * 60 * 1000L
            val fourDaysMillis = 4 * 24 * 60 * 60 * 1000L

            val proximityTasks = pendingTasks.filter { task ->
                val timeDifference = task.deadline - currentTime
                timeDifference in twoDaysMillis..fourDaysMillis
            }

            val proximitySummary = if (proximityTasks.isNotEmpty()) {
                val assignments = proximityTasks.count { it.type.name == "ASSIGNMENT" }
                val exams = proximityTasks.count { it.type.name == "EXAM" }
                "Due in 2-4 days: $assignments Assignments, $exams Exams"
            } else {
                ""
            }

            // 4. Trigger notifications if there is any material to remind about
            if (chapterSummary.isNotEmpty() || taskSummary.isNotEmpty() || proximitySummary.isNotEmpty()) {
                NotificationHelper.sendDailyReminder(
                    context = applicationContext,
                    chapterDetails = chapterSummary,
                    taskDetails = taskSummary,
                    proximityDetails = proximitySummary
                )
            }

            // 5. Dynamic rescheduling to prevent drift ("time-shift" bug)
            // By scheduling the next OneTimeWorkRequest at the end of the current run,
            // we re-calculate the exact time until the next 12:00 PM.
            WorkScheduler.scheduleDailyReminder(applicationContext)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of a temporary failure, retry
            Result.retry()
        }
    }
}
