package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

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
    @PrimaryKey val id: Int = 1,
    // NOTE: userId MUST be set to the real Supabase auth uid right after login/signup.
    // Never persist/sync this entity while userId is blank.
    @Json(name = "user_id") val userId: String = "",
    val username: String = "",
    val email: String = "",
    @Json(name = "joined_date") val joinedDate: String = "",
    val hearts: Int = 5,
    val gems: Int = 0,
    val streak: Int = 0,
    val xp: Int = 0,
    @Json(name = "active_language") val activeLanguage: String = "en",
    @Json(name = "sound_effects_enabled") val soundEffectsEnabled: Boolean = true,
    @Json(name = "animations_enabled") val animationsEnabled: Boolean = true,
    @Json(name = "notifications_enabled") val notificationsEnabled: Boolean = true,
    @Json(name = "streak_freeze_count") val streakFreezeCount: Int = 0,
    val league: String = "البرونزي",
    @Json(name = "best_rank") val bestRank: String = "غير مصنف"
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

// New entities for Supabase tables
@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: Int,
    val userId: String,
    val friendId: String,
    val status: String, // "pending" | "accepted" | "blocked"
    val createdAt: Long,
    val respondedAt: Long?
)

@Entity(tableName = "achievement_types")
data class AchievementTypeEntity(
    @PrimaryKey val id: String,
    val nameAr: String,
    val descriptionAr: String,
    val iconName: String,
    val colorHex: String,
    val conditionJson: String // e.g. {"type": "streak", "target": 10}
)

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val achievementId: String,
    val earnedAt: Long
)

@Entity(tableName = "leaderboard_weekly")
data class LeaderboardWeeklyEntity(
    @PrimaryKey val userId: String,
    val xpEarned: Int,
    val weekStart: String, // ISO date
    val rank: Int?
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val id: Int,
    val date: String, // ISO date
    val templateId: String,
    val titleAr: String,
    val descriptionAr: String,
    val xpReward: Int
)

@Entity(tableName = "user_daily_challenges")
data class UserDailyChallengeEntity(
    @PrimaryKey val id: Int = 0,
    val userId: String,
    val challengeId: Int,
    val completed: Boolean = false,
    val score: Int?,
    val completedAt: Long?
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val frontText: String,
    val backText: String,
    val deckName: String
)

@Entity(tableName = "xp_events")
data class XPEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val xpAmount: Int,
    val source: String, // "lesson", "challenge", "achievement", etc.
    val createdAt: Long
)

@Entity(tableName = "challenge_templates")
data class ChallengeTemplateEntity(
    @PrimaryKey val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val targetType: String, // "lessons_completed", "xp_earned", "words_written", etc.
    val targetValue: Int,
    val xpReward: Int
)

@Entity(tableName = "review_schedule")
data class ReviewScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val flashcardId: Int,
    val easeFactor: Float,
    val intervalDays: Int,
    val repetitions: Int,
    val nextReview: Long
)

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val userId: String,
    val username: String?,
    val email: String?,
    val level: String?,
    val interests: String? // JSON array
)

// === Content tables (public read) ===

data class PhaseEntity(
    val id: Int,
    val code: String,
    val title_ar: String,
    val title_en: String,
    val cefr_level: String,
    val order_index: Int
)

data class UnitEntity(
    val id: Int,
    val phase_id: Int,
    val title_ar: String,
    val title_en: String,
    val description_ar: String,
    val icon: String,
    val order_index: Int
)

data class LessonEntity(
    val id: Int,
    val unit_id: Int,
    val code: String,
    val title_ar: String,
    val title_en: String,
    val cefr_level: String,
    val hook_text: String?,
    val scene_context: String?,
    val dialogue: String?,
    val focus_rule: String?,
    val order_index: Int,
    val estimated_min: Int,
    val is_published: Boolean
)

data class MilestoneTestEntity(
    val id: Int,
    val phase_id: Int,
    val title_ar: String,
    val title_en: String,
    val description_ar: String?,
    val pass_score: Int,
    val time_limit_min: Int,
    val questions: String?
)

// === Per-user state tables (RLS: auth.uid() = user_id) ===

data class HeartsStateEntity(
    val user_id: String,
    val hearts_count: Int,
    val last_refill_at: String
)

data class GemWalletEntity(
    val user_id: String,
    val balance: Int,
    val updated_at: String
)

data class GemTransactionEntity(
    val id: Int,
    val user_id: String,
    val amount: Int,
    val reason: String,
    val created_at: String
)

data class UserStreaksEntity(
    val user_id: String,
    val current_streak: Int,
    val longest_streak: Int,
    val last_activity: String?,
    val total_xp: Int,
    val total_lessons: Int,
    val total_flashcards: Int
)

data class UserLevelsEntity(
    val user_id: String,
    val current_level: Int,
    val current_xp: Int,
    val xp_to_next: Int,
    val title_ar: String?,
    val title_en: String?
)

data class UserSettingsEntity(
    val user_id: String,
    val daily_goal_min: Int,
    val reminder_time: String?,
    val preferred_lang: String,
    val notifications_on: Boolean,
    val streak_reminder_hour: Int?,
    val xp_goal_daily: Int,
    val preferred_difficulty: String?,
    val show_leaderboard: Boolean,
    val audio_speed: Float
)

data class StreakFreezeInventoryEntity(
    val user_id: String,
    val quantity: Int
)

data class PhaseUnlockEntity(
    val user_id: String,
    val phase_id: Int,
    val unlocked_at: String
)

data class BookmarkEntity(
    val id: Int,
    val user_id: String,
    val lesson_id: Int,
    val created_at: String
)

data class ListeningExerciseEntity(
    val id: Int,
    val lesson_id: Int,
    val audio_url: String,
    val transcript: String,
    val transcript_ar: String?,
    val difficulty: String,
    val duration_sec: Int?,
    val accent: String?,
    val order_in_lesson: Int
)

data class PronunciationTargetEntity(
    val id: Int,
    val lesson_id: Int,
    val word_or_phrase: String,
    val ipa: String?,
    val audio_url: String?,
    val common_error_ar: String?,
    val tip_ar: String?
)

data class UserWeakPointEntity(
    val id: Int = 0,
    val user_id: String,
    val skill_tag: String,
    val error_count: Int,
    val accuracy_pct: Float,
    val needs_review: Boolean
)

data class AIChatMessageEntity(
    val id: Int = 0,
    val user_id: String,
    val role: String,
    val content: String,
    val created_at: String?
)

data class AnalyticsEventEntity(
    val id: Int = 0,
    val user_id: String,
    val event_name: String,
    val event_data: String?,
    val created_at: String?
)

data class MilestoneAttemptEntity(
    val id: Int = 0,
    val user_id: String,
    val test_id: Int,
    val score: Int,
    val passed: Boolean,
    val completed_at: String
)

data class MilestoneTestQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String?
)
