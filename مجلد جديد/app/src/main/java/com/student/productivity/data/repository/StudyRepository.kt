package com.student.productivity.data.repository

import com.student.productivity.data.local.dao.AppDao
import com.student.productivity.data.local.entity.AssignmentOrExamEntity
import com.student.productivity.data.local.entity.ChapterEntity
import com.student.productivity.data.local.entity.SemesterEntity
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val appDao: AppDao) {

    // --- Semesters ---
    suspend fun insertSemester(semester: SemesterEntity): Long = appDao.insertSemester(semester)
    suspend fun updateSemester(semester: SemesterEntity) = appDao.updateSemester(semester)
    suspend fun deleteSemester(semester: SemesterEntity) = appDao.deleteSemester(semester)
    suspend fun getSemesterById(id: Long): SemesterEntity? = appDao.getSemesterById(id)
    fun getAllSemesters(): Flow<List<SemesterEntity>> = appDao.getAllSemestersFlow()

    // --- Chapters ---
    suspend fun insertChapter(chapter: ChapterEntity): Long = appDao.insertChapter(chapter)
    suspend fun updateChapter(chapter: ChapterEntity) = appDao.updateChapter(chapter)
    suspend fun deleteChapter(chapter: ChapterEntity) = appDao.deleteChapter(chapter)
    suspend fun getChapterById(id: Long): ChapterEntity? = appDao.getChapterById(id)
    fun getChaptersBySemester(semesterId: Long): Flow<List<ChapterEntity>> = appDao.getChaptersBySemesterFlow(semesterId)
    
    // Chapters needing daily reminders (IN_PROGRESS, SAVED)
    suspend fun getRemindableChapters(): List<ChapterEntity> = appDao.getRemindableChapters()
    fun getRemindableChaptersFlow(): Flow<List<ChapterEntity>> = appDao.getRemindableChaptersFlow()

    // --- Assignments & Exams ---
    suspend fun insertAssignmentOrExam(task: AssignmentOrExamEntity): Long = appDao.insertAssignmentOrExam(task)
    suspend fun updateAssignmentOrExam(task: AssignmentOrExamEntity) = appDao.updateAssignmentOrExam(task)
    suspend fun deleteAssignmentOrExam(task: AssignmentOrExamEntity) = appDao.deleteAssignmentOrExam(task)
    suspend fun getAssignmentOrExamById(id: Long): AssignmentOrExamEntity? = appDao.getAssignmentOrExamById(id)
    fun getTasksBySemester(semesterId: Long): Flow<List<AssignmentOrExamEntity>> = appDao.getTasksBySemesterFlow(semesterId)
    
    // Pending (uncompleted) assignments & exams
    suspend fun getPendingTasks(): List<AssignmentOrExamEntity> = appDao.getPendingTasks()
    fun getPendingTasksFlow(): Flow<List<AssignmentOrExamEntity>> = appDao.getPendingTasksFlow()
    
    // Tasks due within a specific timeframe (e.g. proximity 2-4 days)
    suspend fun getPendingTasksDueBetween(startTime: Long, endTime: Long): List<AssignmentOrExamEntity> {
        return appDao.getPendingTasksDueBetween(startTime, endTime)
    }
}
