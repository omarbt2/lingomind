package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val exerciseType: String, // "mcq" | "fill_blank" | "reorder" | "transform" | "free_writing"
    val orderInLesson: Int, // 1 to 5
    val promptAr: String,
    val promptEn: String?,
    val contentJson: String, // Stringified JSON structure representing content
    val correctAnswer: String?,
    val explanationAr: String,
    val difficulty: String, // "easy" | "medium" | "hard"
    val skillTags: String // Comma-separated tags e.g. "present_simple,negation"
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1, // Single user profile for prototype
    val username: String = "Omar",
    val email: String = "ombt2010@gmail.com",
    val joinedDate: String = "أكتوبر 2021",
    val hearts: Int = 5,
    val gems: Int = 1264,
    val streak: Int = 5,
    val xp: Int = 887,
    val activeLanguage: String = "en",
    val soundEffectsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val streakFreezeCount: Int = 2
)

@Entity(tableName = "wrong_answers")
data class WrongAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val userAnswer: String,
    val correctAnswer: String,
    val skillTags: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "writing_submissions")
data class WritingSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val topicAr: String,
    val userText: String,
    val wordCount: Int,
    val status: String = "Awaiting AI feedback", // "Awaiting AI feedback" | "Evaluated"
    val aiFeedback: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
