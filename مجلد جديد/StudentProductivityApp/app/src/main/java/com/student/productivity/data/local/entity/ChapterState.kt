package com.student.productivity.data.local.entity

/**
 * Represents the three possible states of an academic chapter.
 */
enum class ChapterState(val displayName: String) {
    IN_PROGRESS("Jari Al-Amal"),      // Active study, requires daily reminders
    SAVED("Tam Al-Hifz"),            // Saved/memorized, requires reminders
    COMPLETED("Tam Al-Tarkhees")     // Completed/archived, no reminders
}
