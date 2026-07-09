package com.example.data

import kotlinx.coroutines.flow.Flow

class LingoMindRepository(private val dao: LingoMindDao) {
    fun getExercisesForLesson(lessonId: Int): Flow<List<ExerciseEntity>> {
        return dao.getExercisesForLesson(lessonId)
    }

    suspend fun getExercisesForLessonDirect(lessonId: Int): List<ExerciseEntity> {
        return dao.getExercisesForLessonDirect(lessonId)
    }

    fun getUserProgressFlow(): Flow<UserProgressEntity?> {
        return dao.getUserProgressFlow()
    }

    suspend fun getUserProgress(): UserProgressEntity? {
        return dao.getUserProgress()
    }

    suspend fun updateUserProgress(progress: UserProgressEntity) {
        dao.insertUserProgress(progress)
        SupabaseClient.syncUserProgress(progress)
    }

    suspend fun insertWrongAnswer(wrongAnswer: WrongAnswerEntity) {
        dao.insertWrongAnswer(wrongAnswer)
    }

    fun getAllWrongAnswers(): Flow<List<WrongAnswerEntity>> {
        return dao.getAllWrongAnswers()
    }

    suspend fun clearWrongAnswers() {
        dao.clearWrongAnswers()
    }

    suspend fun insertWritingSubmission(submission: WritingSubmissionEntity) {
        dao.insertWritingSubmission(submission)
        SupabaseClient.syncWritingSubmission(submission)
    }

    fun getAllWritingSubmissions(): Flow<List<WritingSubmissionEntity>> {
        return dao.getAllWritingSubmissions()
    }
}
