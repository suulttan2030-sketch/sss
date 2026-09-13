package com.student.productivity.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.student.productivity.data.local.entity.AssignmentOrExamEntity
import com.student.productivity.data.local.entity.ChapterEntity
import com.student.productivity.data.local.entity.SemesterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Semesters ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity): Long

    @Update
    suspend fun updateSemester(semester: SemesterEntity)

    @Delete
    suspend fun deleteSemester(semester: SemesterEntity)

    @Query("SELECT * FROM semesters WHERE id = :id")
    suspend fun getSemesterById(id: Long): SemesterEntity?

    @Query("SELECT * FROM semesters ORDER BY startDate DESC")
    fun getAllSemestersFlow(): Flow<List<SemesterEntity>>


    // --- Chapters ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Long): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE semesterId = :semesterId")
    fun getChaptersBySemesterFlow(semesterId: Long): Flow<List<ChapterEntity>>

    /**
     * Gets all chapters that require study reminders.
     * Chapters in 'IN_PROGRESS' (Jari Al-Amal) and 'SAVED' (Tam Al-Hifz) require reminders.
     * Chapters in 'COMPLETED' (Tam Al-Tarkhees) are excluded.
     */
    @Query("SELECT * FROM chapters WHERE state != 'COMPLETED'")
    suspend fun getRemindableChapters(): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE state != 'COMPLETED'")
    fun getRemindableChaptersFlow(): Flow<List<ChapterEntity>>


    // --- Assignments & Exams ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignmentOrExam(task: AssignmentOrExamEntity): Long

    @Update
    suspend fun updateAssignmentOrExam(task: AssignmentOrExamEntity)

    @Delete
    suspend fun deleteAssignmentOrExam(task: AssignmentOrExamEntity)

    @Query("SELECT * FROM assignments_and_exams WHERE id = :id")
    suspend fun getAssignmentOrExamById(id: Long): AssignmentOrExamEntity?

    @Query("SELECT * FROM assignments_and_exams WHERE semesterId = :semesterId ORDER BY deadline ASC")
    fun getTasksBySemesterFlow(semesterId: Long): Flow<List<AssignmentOrExamEntity>>

    /**
     * Retrieves all pending (uncompleted) assignments and exams.
     */
    @Query("SELECT * FROM assignments_and_exams WHERE isCompleted = 0 ORDER BY deadline ASC")
    suspend fun getPendingTasks(): List<AssignmentOrExamEntity>

    @Query("SELECT * FROM assignments_and_exams WHERE isCompleted = 0 ORDER BY deadline ASC")
    fun getPendingTasksFlow(): Flow<List<AssignmentOrExamEntity>>

    /**
     * Retrieves pending tasks due within a specific time window.
     * Useful for proximity reminders (e.g., due in 2-4 days).
     */
    @Query("SELECT * FROM assignments_and_exams WHERE isCompleted = 0 AND deadline BETWEEN :startTimesMs AND :endTimesMs ORDER BY deadline ASC")
    suspend fun getPendingTasksDueBetween(startTimesMs: Long, endTimesMs: Long): List<AssignmentOrExamEntity>
}
