package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface semaDao {
    // Exercises
    @Query("SELECT * FROM exercises WHERE lessonId = :lessonId ORDER BY orderInLesson ASC")
    fun getExercisesForLesson(lessonId: Int): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE lessonId = :lessonId ORDER BY orderInLesson ASC")
    suspend fun getExercisesForLessonDirect(lessonId: Int): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgressFlow(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgress(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgressEntity)

    // Wrong Answers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongAnswer(wrongAnswer: WrongAnswerEntity)

    @Query("SELECT * FROM wrong_answers ORDER BY timestamp DESC")
    fun getAllWrongAnswers(): Flow<List<WrongAnswerEntity>>

    @Query("DELETE FROM wrong_answers")
    suspend fun clearWrongAnswers()

    // Writing Submissions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWritingSubmission(submission: WritingSubmissionEntity)

    @Query("SELECT * FROM writing_submissions ORDER BY timestamp DESC")
    fun getAllWritingSubmissions(): Flow<List<WritingSubmissionEntity>>
}
