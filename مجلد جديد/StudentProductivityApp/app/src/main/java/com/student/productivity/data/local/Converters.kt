package com.student.productivity.data.local

import androidx.room.TypeConverter
import com.student.productivity.data.local.entity.ChapterState
import com.student.productivity.data.local.entity.TaskType

class Converters {

    @TypeConverter
    fun fromChapterState(state: ChapterState): String {
        return state.name
    }

    @TypeConverter
    fun toChapterState(value: String): ChapterState {
        return try {
            ChapterState.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ChapterState.IN_PROGRESS
        }
    }

    @TypeConverter
    fun fromTaskType(type: TaskType): String {
        return type.name
    }

    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return try {
            TaskType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TaskType.ASSIGNMENT
        }
    }
}
