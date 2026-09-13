package com.student.productivity

import com.student.productivity.data.local.entity.AssignmentOrExamEntity
import com.student.productivity.data.local.entity.TaskType
import com.student.productivity.worker.WorkScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class WorkSchedulerTest {

    @Test
    fun testCalculateDelay_beforeTwelvePM() {
        // Create calendar for 9:00 AM on a specific day
        val currentCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = currentCalendar.timeInMillis

        // Expected delay: 3 hours (9 AM to 12 PM)
        val expectedDelay = 3 * 60 * 60 * 1000L // 3 hours in ms

        val actualDelay = WorkScheduler.calculateDelayToTwelvePM(now)

        assertEquals(expectedDelay, actualDelay)
    }

    @Test
    fun testCalculateDelay_afterTwelvePM() {
        // Create calendar for 3:00 PM on a specific day
        val currentCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = currentCalendar.timeInMillis

        // Expected delay: 21 hours (from 3 PM today to 12 PM tomorrow)
        val expectedDelay = 21 * 60 * 60 * 1000L // 21 hours in ms

        val actualDelay = WorkScheduler.calculateDelayToTwelvePM(now)

        assertEquals(expectedDelay, actualDelay)
    }

    @Test
    fun testCalculateDelay_exactlyTwelvePM() {
        // Create calendar for exactly 12:00 PM
        val currentCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = currentCalendar.timeInMillis

        // Expected delay: 24 hours (exactly next day at 12 PM)
        val expectedDelay = 24 * 60 * 60 * 1000L

        val actualDelay = WorkScheduler.calculateDelayToTwelvePM(now)

        assertEquals(expectedDelay, actualDelay)
    }

    @Test
    fun testProximityFilteringLogic() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // Due tomorrow (1 day away) -> Not in 2-4 day proximity
        val taskDueTomorrow = AssignmentOrExamEntity(
            id = 1,
            semesterId = 1,
            title = "Task 1",
            type = TaskType.ASSIGNMENT,
            deadline = now + oneDayMillis,
            isCompleted = false
        )

        // Due in 3 days -> In 2-4 day proximity
        val taskDueThreeDays = AssignmentOrExamEntity(
            id = 2,
            semesterId = 1,
            title = "Task 2",
            type = TaskType.ASSIGNMENT,
            deadline = now + (3 * oneDayMillis),
            isCompleted = false
        )

        // Due in 5 days -> Not in 2-4 day proximity
        val taskDueFiveDays = AssignmentOrExamEntity(
            id = 3,
            semesterId = 1,
            title = "Task 3",
            type = TaskType.ASSIGNMENT,
            deadline = now + (5 * oneDayMillis),
            isCompleted = false
        )

        val pendingTasks = listOf(taskDueTomorrow, taskDueThreeDays, taskDueFiveDays)

        // Math matching Worker logic:
        val twoDaysMillis = 2 * oneDayMillis
        val fourDaysMillis = 4 * oneDayMillis

        val proximityTasks = pendingTasks.filter { task ->
            val timeDifference = task.deadline - now
            timeDifference in twoDaysMillis..fourDaysMillis
        }

        assertEquals(1, proximityTasks.size)
        assertEquals("Task 2", proximityTasks[0].title)
    }
}
