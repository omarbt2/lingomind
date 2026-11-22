package com.example.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

// JSON parsing helper models
data class McqContent(val options: List<String>, val correct_index: Int)
data class FillBlankContent(val sentence: String, val word_bank: List<String>)
data class ReorderContent(val shuffled_words: List<String>)
data class TransformContent(val original_sentence: String, val instruction_ar: String)
data class FreeWritingContent(val min_words: Int, val topic_ar: String)
data class ListeningContent(val audio_url: String, val transcript: String, val question: String, val options: List<String>, val correct_index: Int)

// Loading and error states
sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Success(val message: String = "") : UiState()
    data class Error(val message: String) : UiState()
}

class semaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: semaRepository
    private val dao: semaDao
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Database Flows
    val userProgress: StateFlow<UserProgressEntity?>
    val wrongAnswers: StateFlow<List<WrongAnswerEntity>>
    val writingSubmissions: StateFlow<List<WritingSubmissionEntity>>

    // Supabase Flows - using MutableStateFlow for manual updates
    private val _friends = MutableStateFlow<List<FriendEntity>>(emptyList())
    val friends: StateFlow<List<FriendEntity>> = _friends

    private val _achievements = MutableStateFlow<List<AchievementTypeEntity>>(emptyList())
    val achievements: StateFlow<List<AchievementTypeEntity>> = _achievements

    private val _userAchievements = MutableStateFlow<List<UserAchievementEntity>>(emptyList())
    val userAchievements: StateFlow<List<UserAchievementEntity>> = _userAchievements

    private val _leaderboard = MutableStateFlow<List<LeaderboardWeeklyEntity>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardWeeklyEntity>> = _leaderboard

    private val _todaysChallenge = MutableStateFlow<DailyChallengeEntity?>(null)
    val todaysChallenge: StateFlow<DailyChallengeEntity?> = _todaysChallenge

    private val _userDailyChallenge = MutableStateFlow<UserDailyChallengeEntity?>(null)
    val userDailyChallenge: StateFlow<UserDailyChallengeEntity?> = _userDailyChallenge

    private val _flashcards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val flashcards: StateFlow<List<FlashcardEntity>> = _flashcards

    private val _xpEvents = MutableStateFlow<List<XPEventEntity>>(emptyList())
    val xpEvents: StateFlow<List<XPEventEntity>> = _xpEvents

    private val _reviewSchedule = MutableStateFlow<List<ReviewScheduleEntity>>(emptyList())
    val reviewSchedule: StateFlow<List<ReviewScheduleEntity>> = _reviewSchedule

    // === Real Supabase table state ===
    private val _phases = MutableStateFlow<List<PhaseEntity>>(emptyList())
    val phases: StateFlow<List<PhaseEntity>> = _phases

    private val _units = MutableStateFlow<List<UnitEntity>>(emptyList())
    val units: StateFlow<List<UnitEntity>> = _units

    private val _lessons = MutableStateFlow<List<LessonEntity>>(emptyList())
    val lessons: StateFlow<List<LessonEntity>> = _lessons

    private val _heartsState = MutableStateFlow<HeartsStateEntity?>(null)
    val heartsState: StateFlow<HeartsStateEntity?> = _heartsState

    private val _gemWallet = MutableStateFlow<GemWalletEntity?>(null)
    val gemWallet: StateFlow<GemWalletEntity?> = _gemWallet

    private val _userStreaks = MutableStateFlow<UserStreaksEntity?>(null)
    val userStreaks: StateFlow<UserStreaksEntity?> = _userStreaks

    private val _userLevels = MutableStateFlow<UserLevelsEntity?>(null)
    val userLevels: StateFlow<UserLevelsEntity?> = _userLevels

    private val _phaseUnlocks = MutableStateFlow<List<PhaseUnlockEntity>>(emptyList())
    val phaseUnlocks: StateFlow<List<PhaseUnlockEntity>> = _phaseUnlocks

    private val _streakFreeze = MutableStateFlow<StreakFreezeInventoryEntity?>(null)
    val streakFreeze: StateFlow<StreakFreezeInventoryEntity?> = _streakFreeze

    // Milestone Test State
    private val _milestoneTest = MutableStateFlow<MilestoneTestEntity?>(null)
    val milestoneTest: StateFlow<MilestoneTestEntity?> = _milestoneTest

    private val _milestoneQuestions = MutableStateFlow<List<MilestoneTestQuestion>>(emptyList())
    val milestoneQuestions: StateFlow<List<MilestoneTestQuestion>> = _milestoneQuestions

    private val _milestoneQuestionIndex = MutableStateFlow(0)
    val milestoneQuestionIndex: StateFlow<Int> = _milestoneQuestionIndex

    private val _milestoneSelectedAnswers = MutableStateFlow<MutableMap<Int, Int>>(mutableMapOf())
    val milestoneSelectedAnswers: StateFlow<Map<Int, Int>> = _milestoneSelectedAnswers

    private val _milestoneScore = MutableStateFlow(0)
    val milestoneScore: StateFlow<Int> = _milestoneScore

    private val _milestoneTestFinished = MutableStateFlow(false)
    val milestoneTestFinished: StateFlow<Boolean> = _milestoneTestFinished

    private val _milestoneTestPassed = MutableStateFlow(false)
    val milestoneTestPassed: StateFlow<Boolean> = _milestoneTestPassed

    private val _milestoneAnswerChecked = MutableStateFlow(false)
    val milestoneAnswerChecked: StateFlow<Boolean> = _milestoneAnswerChecked

    private val _milestoneSelectedOption = MutableStateFlow<Int?>(null)
    val milestoneSelectedOption: StateFlow<Int?> = _milestoneSelectedOption

    // User Settings State
    private val _userSettings = MutableStateFlow<UserSettingsEntity?>(null)
    val userSettings: StateFlow<UserSettingsEntity?> = _userSettings

    // Bookmarks State
    private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks

    private val _bookmarkedLessonIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedLessonIds: StateFlow<Set<Int>> = _bookmarkedLessonIds

    // Gem Transactions State
    private val _gemTransactions = MutableStateFlow<List<GemTransactionEntity>>(emptyList())
    val gemTransactions: StateFlow<List<GemTransactionEntity>> = _gemTransactions

    // Weak Points State
    private val _weakPoints = MutableStateFlow<List<UserWeakPointEntity>>(emptyList())
    val weakPoints: StateFlow<List<UserWeakPointEntity>> = _weakPoints

    // Listening Exercises State
    private val _listeningExercises = MutableStateFlow<List<ListeningExerciseEntity>>(emptyList())
    val listeningExercises: StateFlow<List<ListeningExerciseEntity>> = _listeningExercises

    // Pronunciation State
    private val _pronunciationTargets = MutableStateFlow<List<PronunciationTargetEntity>>(emptyList())
    val pronunciationTargets: StateFlow<List<PronunciationTargetEntity>> = _pronunciationTargets

    // AI Chat State
    private val _aiMessages = MutableStateFlow<List<AIChatMessageEntity>>(emptyList())
    val aiMessages: StateFlow<List<AIChatMessageEntity>> = _aiMessages

    // All Daily Challenges State
    private val _allDailyChallenges = MutableStateFlow<List<DailyChallengeEntity>>(emptyList())
    val allDailyChallenges: StateFlow<List<DailyChallengeEntity>> = _allDailyChallenges

    private val _allUserDailyChallenges = MutableStateFlow<List<UserDailyChallengeEntity>>(emptyList())
    val allUserDailyChallenges: StateFlow<List<UserDailyChallengeEntity>> = _allUserDailyChallenges

    // Milestone Attempts State
    private val _milestoneAttempts = MutableStateFlow<List<MilestoneAttemptEntity>>(emptyList())
    val milestoneAttempts: StateFlow<List<MilestoneAttemptEntity>> = _milestoneAttempts

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Auth state
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    init {
        // Initialize Supabase client
        SupabaseClient.init(application)

        val database = AppDatabase.getInstance(application)
        dao = database.semaDao()
        repository = semaRepository(dao)

        userProgress = repository.getUserProgressFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        wrongAnswers = repository.getAllWrongAnswers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        writingSubmissions = repository.getAllWritingSubmissions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Check authentication status
        _isAuthenticated.value = SupabaseClient.isAuthenticated()

        // Bidirectional Sync on startup
        syncRemoteData()
    }

    private fun syncRemoteData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Fetch public content immediately (phases, units, lessons — no auth needed)
            fetchAllContent()
            fetchAchievementTypes()
            fetchFlashcards()

            // Retry loop: wait for session to be ready (max ~1.5s)
            var userId = SupabaseClient.getCurrentUserId()
            var attempts = 0
            while (userId == null && attempts < 5) {
                delay(300)
                userId = SupabaseClient.getCurrentUserId()
                attempts++
            }

            if (userId == null) {
                _isLoading.value = false
                _errorMessage.value = "المستخدم غير مسجل الدخول"
                return@launch
            }

            // Clear any stale error now that we have a session
            _errorMessage.value = null

            try {
                // Fetch legacy user_progress (for backward compat)
                SupabaseClient.fetchUserProgress(userId) { remoteProgress ->
                    viewModelScope.launch {
                        if (remoteProgress != null) {
                            dao.insertUserProgress(remoteProgress)
                        }
                    }
                }
                // Fetch all user-specific state
                fetchAllUserState()
                fetchUserAchievements()
                fetchFriends()
                fetchTodaysChallenge()
                fetchUserDailyChallenge()
                fetchXPEvents()
                fetchReviewSchedule()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failed to sync data: ${e.message}"
            }
        }
    }

    // Fetch friends from Supabase
    fun fetchFriends() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchFriends(userId) { list ->
            viewModelScope.launch {
                _friends.value = list ?: emptyList()
            }
        }
    }

    // Send friend request
    fun sendFriendRequest(friendId: String) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.sendFriendRequest(userId, friendId) { result ->
            if (result.isSuccess) {
                fetchFriends()
            }
        }
    }

    // Accept/decline friend request
    fun respondToFriendRequest(requestId: Int, status: String) {
        SupabaseClient.respondToFriendRequest(requestId, status) { result ->
            if (result.isSuccess) {
                viewModelScope.launch {
                    fetchFriends()
                }
            }
        }
    }

    // Fetch achievement types
    fun fetchAchievementTypes() {
        SupabaseClient.fetchAchievementTypes { list ->
            viewModelScope.launch {
                _achievements.value = list ?: emptyList()
            }
        }
    }

    // Fetch user achievements
    fun fetchUserAchievements() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserAchievements(userId) { list ->
            viewModelScope.launch {
                _userAchievements.value = list ?: emptyList()
            }
        }
    }

    // Fetch weekly leaderboard
    fun fetchWeeklyLeaderboard() {
        SupabaseClient.fetchWeeklyLeaderboard(30) { list ->
            viewModelScope.launch {
                _leaderboard.value = list ?: emptyList()
            }
        }
    }

    // Fetch today's daily challenge
    fun fetchTodaysChallenge() {
        val date = java.time.LocalDate.now().toString()
        SupabaseClient.fetchTodaysChallenge(date) { challenge ->
            viewModelScope.launch {
                _todaysChallenge.value = challenge
            }
        }
    }

    // Fetch user's status for today's challenge
    fun fetchUserDailyChallenge() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        _todaysChallenge.value?.let { challenge ->
            SupabaseClient.fetchUserDailyChallenge(userId, challenge.id) { userChallenge ->
                viewModelScope.launch {
                    _userDailyChallenge.value = userChallenge
                }
            }
        }
    }

    // Complete daily challenge
    fun completeDailyChallenge(score: Int) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        _todaysChallenge.value?.let { challenge ->
            SupabaseClient.completeDailyChallenge(userId, challenge.id, score) { result ->
                if (result.isSuccess) {
                    viewModelScope.launch {
                        fetchUserDailyChallenge()
                        fetchTodaysChallenge()
                    }
                }
            }
        }
    }

    // Fetch flashcards
    fun fetchFlashcards() {
        SupabaseClient.fetchFlashcards { list ->
            viewModelScope.launch {
                _flashcards.value = list ?: emptyList()
            }
        }
    }

    // Fetch XP events for streak calendar
    fun fetchXPEvents() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchXPEvents(userId) { list ->
            viewModelScope.launch {
                _xpEvents.value = list ?: emptyList()
            }
        }
    }

    // Update review schedule (SM-2)
    fun updateReviewSchedule(flashcardId: Int, easeFactor: Float, intervalDays: Int, repetitions: Int, nextReview: Long) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.updateReviewSchedule(userId, flashcardId, easeFactor, intervalDays, repetitions, nextReview) { _ ->
            // Silent fail for now
        }
    }

    // Fetch review schedule
    fun fetchReviewSchedule() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchReviewSchedule(userId) { list ->
            viewModelScope.launch {
                _reviewSchedule.value = list ?: emptyList()
            }
        }
    }

    // === Real table fetchers ===

    fun fetchPhases() {
        SupabaseClient.fetchPhases { list ->
            viewModelScope.launch { _phases.value = list ?: emptyList() }
        }
    }

    fun fetchUnits(phaseId: Int? = null) {
        SupabaseClient.fetchUnits(phaseId) { list ->
            viewModelScope.launch { _units.value = list ?: emptyList() }
        }
    }

    fun fetchLessons(unitId: Int? = null) {
        SupabaseClient.fetchLessons(unitId) { list ->
            viewModelScope.launch { _lessons.value = list ?: emptyList() }
        }
    }

    fun fetchHeartsState() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchHeartsState(userId) { state ->
            viewModelScope.launch { _heartsState.value = state }
        }
    }

    fun fetchGemWallet() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchGemWallet(userId) { wallet ->
            viewModelScope.launch { _gemWallet.value = wallet }
        }
    }

    fun fetchUserStreaksData() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserStreaks(userId) { streaks ->
            viewModelScope.launch { _userStreaks.value = streaks }
        }
    }

    fun fetchUserLevelsData() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserLevels(userId) { levels ->
            viewModelScope.launch { _userLevels.value = levels }
        }
    }

    fun fetchPhaseUnlocksData() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchPhaseUnlocks(userId) { unlocks ->
            viewModelScope.launch { _phaseUnlocks.value = unlocks ?: emptyList() }
        }
    }

    fun fetchStreakFreezeData() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchStreakFreezeInventory(userId) { freeze ->
            viewModelScope.launch { _streakFreeze.value = freeze }
        }
    }

    fun fetchAllContent() {
        fetchPhases()
        fetchUnits()
        fetchLessons()
    }

    fun fetchAllUserState() {
        fetchHeartsState()
        fetchGemWallet()
        fetchUserStreaksData()
        fetchUserLevelsData()
        fetchPhaseUnlocksData()
        fetchStreakFreezeData()
    }

    // === Milestone Test Functions ===

    fun fetchMilestoneTest(phaseId: Int) {
        SupabaseClient.fetchMilestoneTest(phaseId) { test ->
            viewModelScope.launch {
                _milestoneTest.value = test
                if (test != null && test.questions != null) {
                    parseMilestoneQuestions(test.questions)
                }
                _milestoneQuestionIndex.value = 0
                _milestoneSelectedAnswers.value = mutableMapOf()
                _milestoneScore.value = 0
                _milestoneTestFinished.value = false
                _milestoneTestPassed.value = false
                _milestoneAnswerChecked.value = false
                _milestoneSelectedOption.value = null
            }
        }
    }

    private fun parseMilestoneQuestions(questionsJson: String) {
        try {
            val questionsList = com.squareup.moshi.Types.newParameterizedType(List::class.java, MilestoneTestQuestion::class.java)
            val adapter = moshi.adapter<List<MilestoneTestQuestion>>(questionsList)
            val parsed = adapter.fromJson(questionsJson)
            _milestoneQuestions.value = parsed ?: emptyList()
        } catch (e: Exception) {
            Log.e("MilestoneTest", "Failed to parse questions: ${e.message}")
            _milestoneQuestions.value = emptyList()
        }
    }

    fun selectMilestoneOption(index: Int) {
        if (!_milestoneAnswerChecked.value) {
            _milestoneSelectedOption.value = index
        }
    }

    fun confirmMilestoneAnswer() {
        val currentIndex = _milestoneQuestionIndex.value
        val selected = _milestoneSelectedOption.value ?: return
        val questions = _milestoneQuestions.value
        if (currentIndex >= questions.size) return

        val correct = selected == questions[currentIndex].correctIndex
        val updatedAnswers = _milestoneSelectedAnswers.value.toMutableMap()
        updatedAnswers[currentIndex] = selected
        _milestoneSelectedAnswers.value = updatedAnswers
        _milestoneAnswerChecked.value = true

        if (correct) {
            _milestoneScore.value = _milestoneScore.value + 1
        }
    }

    fun nextMilestoneQuestion() {
        val nextIndex = _milestoneQuestionIndex.value + 1
        if (nextIndex < _milestoneQuestions.value.size) {
            _milestoneQuestionIndex.value = nextIndex
            _milestoneSelectedOption.value = null
            _milestoneAnswerChecked.value = false
        }
    }

    fun submitMilestoneTest() {
        val totalQuestions = _milestoneQuestions.value.size
        val score = _milestoneScore.value
        val test = _milestoneTest.value ?: return
        val passScore = test.pass_score
        val passed = score >= passScore

        _milestoneTestFinished.value = true
        _milestoneTestPassed.value = passed

        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            SupabaseClient.insertMilestoneAttempt(userId, test.id, score, passed) { _ -> }
            if (passed) {
                SupabaseClient.upsertPhaseUnlock(userId, test.phase_id) { _ ->
                    fetchPhaseUnlocksData()
                }
            }
        }
    }

    fun resetMilestoneTest() {
        _milestoneTest.value = null
        _milestoneQuestions.value = emptyList()
        _milestoneQuestionIndex.value = 0
        _milestoneSelectedAnswers.value = mutableMapOf()
        _milestoneScore.value = 0
        _milestoneTestFinished.value = false
        _milestoneTestPassed.value = false
        _milestoneAnswerChecked.value = false
        _milestoneSelectedOption.value = null
    }

    // === User Settings ===

    fun fetchUserSettings() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserSettings(userId) { settings ->
            viewModelScope.launch { _userSettings.value = settings }
        }
    }

    fun updateUserSettings(settings: UserSettingsEntity) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            SupabaseClient.updateUserSettings(userId, settings) { _ -> }
            _userSettings.value = settings
        }
    }

    // === Bookmarks ===

    fun fetchBookmarks() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchBookmarks(userId) { list ->
            viewModelScope.launch {
                val bookmarksList = list ?: emptyList()
                _bookmarks.value = bookmarksList
                _bookmarkedLessonIds.value = bookmarksList.map { it.lesson_id }.toSet()
            }
        }
    }

    fun toggleBookmark(lessonId: Int) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            if (lessonId in _bookmarkedLessonIds.value) {
                SupabaseClient.removeBookmark(userId, lessonId) { _ ->
                    fetchBookmarks()
                }
            } else {
                SupabaseClient.addBookmark(userId, lessonId) { _ ->
                    fetchBookmarks()
                }
            }
        }
    }

    fun isLessonBookmarked(lessonId: Int): Boolean = lessonId in _bookmarkedLessonIds.value

    // === Gem Transactions ===

    fun fetchGemTransactions() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchGemTransactions(userId) { list ->
            viewModelScope.launch { _gemTransactions.value = list ?: emptyList() }
        }
    }

    // === Weak Points ===

    fun fetchWeakPoints() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserWeakPoints(userId) { list ->
            viewModelScope.launch { _weakPoints.value = list ?: emptyList() }
        }
    }

    fun insertWritingSubmission(exerciseId: Int, topicAr: String, userText: String, wordCount: Int) {
        viewModelScope.launch {
            val submission = WritingSubmissionEntity(
                exerciseId = exerciseId,
                topicAr = topicAr,
                userText = userText,
                wordCount = wordCount,
                status = "Awaiting AI feedback"
            )
            repository.insertWritingSubmission(submission)

            val userId = SupabaseClient.getCurrentUserId()
            if (userId != null) {
                SupabaseClient.syncWritingSubmission(submission)
            }
        }
    }

    private fun trackWeakPoint(skillTags: String, wasCorrect: Boolean) {
        if (wasCorrect) return
        val userId = SupabaseClient.getCurrentUserId() ?: return
        val tags = skillTags.split(",").map { it.trim() }.filter { it.isNotBlank() }
        viewModelScope.launch {
            for (tag in tags) {
                SupabaseClient.upsertWeakPoint(userId, tag, 1, 0.0f) { _ -> }
            }
            fetchWeakPoints()
        }
    }

    // === Listening Exercises ===

    fun fetchListeningExercises(lessonId: Int) {
        SupabaseClient.fetchListeningExercises(lessonId) { list ->
            viewModelScope.launch { _listeningExercises.value = list ?: emptyList() }
        }
    }

    // === Pronunciation ===

    fun fetchPronunciationTargets(lessonId: Int) {
        SupabaseClient.fetchPronunciationTargets(lessonId) { list ->
            viewModelScope.launch { _pronunciationTargets.value = list ?: emptyList() }
        }
    }

    fun insertPronunciationAttempt(targetId: Int, audioUrl: String, score: Float) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.insertPronunciationAttempt(userId, targetId, audioUrl, score) { _ -> }
    }

    // === AI Chat ===

    fun fetchAIMessages() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchAIMessages(userId) { list ->
            viewModelScope.launch { _aiMessages.value = list ?: emptyList() }
        }
    }

    fun sendAIMessage(content: String) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            // Save user message
            SupabaseClient.insertAIMessage(userId, "user", content) { _ -> }

            val userMsg = AIChatMessageEntity(user_id = userId, role = "user", content = content, created_at = null)
            _aiMessages.value = _aiMessages.value + userMsg

            // TODO: Replace with real LLM API call (Claude/GPT via Edge Function)
            // For now, use canned responses
            val reply = getCannedResponse(content)
            SupabaseClient.insertAIMessage(userId, "assistant", reply) { _ -> }

            val assistantMsg = AIChatMessageEntity(user_id = userId, role = "assistant", content = reply, created_at = null)
            _aiMessages.value = _aiMessages.value + assistantMsg
        }
    }

    private fun getCannedResponse(userMessage: String): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("مرحبا") ->
                "Hello! I'm your English learning assistant. How can I help you today?"
            lower.contains("how are you") || lower.contains("كيف حالك") ->
                "I'm doing great! Ready to help you practice English. What would you like to work on?"
            lower.contains("grammar") || lower.contains("قواعد") ->
                "Great topic! Grammar is the foundation of a language. Try to focus on one rule at a time. Which grammar point would you like to discuss?"
            lower.contains("vocabulary") || lower.contains("مفردات") ->
                "Building vocabulary is essential! Try learning 5 new words each day and use them in sentences. What words are you interested in?"
            lower.contains("practice") || lower.contains("تمرين") ->
                "Practice makes perfect! Would you like to practice a conversation, review some vocabulary, or work on grammar?"
            else ->
                "That's interesting! Keep practicing and you'll improve your English skills step by step. Is there a specific topic you'd like help with?"
        }
    }

    // === All Daily Challenges ===

    fun fetchAllDailyChallenges() {
        SupabaseClient.fetchAllDailyChallenges { list ->
            viewModelScope.launch { _allDailyChallenges.value = list ?: emptyList() }
        }
    }

    fun fetchAllUserDailyChallenges() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchUserDailyChallenges(userId) { list ->
            viewModelScope.launch { _allUserDailyChallenges.value = list ?: emptyList() }
        }
    }

    // === Milestone Attempts ===

    fun fetchMilestoneAttempts(testId: Int) {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        SupabaseClient.fetchMilestoneAttempts(userId, testId) { list ->
            viewModelScope.launch { _milestoneAttempts.value = list ?: emptyList() }
        }
    }

    // Active Exercise Session State
    private val _currentExercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val currentExercises: StateFlow<List<ExerciseEntity>> = _currentExercises

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex

    // MCQ specific state
    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex

    // Fill blank / Reorder state
    private val _selectedWordChips = MutableStateFlow<List<String>>(emptyList())
    val selectedWordChips: StateFlow<List<String>> = _selectedWordChips

    private val _remainingWordBank = MutableStateFlow<List<String>>(emptyList())
    val remainingWordBank: StateFlow<List<String>> = _remainingWordBank

    // Text inputs (for Transform & Free writing)
    private val _textAnswer = MutableStateFlow("")
    val textAnswer: StateFlow<String> = _textAnswer

    // Check & Feedback states
    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked

    private val _isAnswerCorrect = MutableStateFlow(false)
    val isAnswerCorrect: StateFlow<Boolean> = _isAnswerCorrect

    private val _showFeedbackBar = MutableStateFlow(false)
    val showFeedbackBar: StateFlow<Boolean> = _showFeedbackBar

    private val _heartsLocal = MutableStateFlow(5)
    val heartsLocal: StateFlow<Int> = _heartsLocal

    private val _xpGainedInSession = MutableStateFlow(0)
    val xpGainedInSession: StateFlow<Int> = _xpGainedInSession

    private val _gemsGainedInSession = MutableStateFlow(0)
    val gemsGainedInSession: StateFlow<Int> = _gemsGainedInSession

    private val _isLessonFinished = MutableStateFlow(false)
    val isLessonFinished: StateFlow<Boolean> = _isLessonFinished

    // Loaded contents parsed
    val activeMcqContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "mcq") {
            try {
                moshi.adapter(McqContent::class.java).fromJson(list[index].contentJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeFillBlankContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "fill_blank") {
            try {
                moshi.adapter(FillBlankContent::class.java).fromJson(list[index].contentJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeReorderContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "reorder") {
            try {
                moshi.adapter(ReorderContent::class.java).fromJson(list[index].contentJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTransformContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "transform") {
            try {
                moshi.adapter(TransformContent::class.java).fromJson(list[index].contentJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeFreeWritingContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "free_writing") {
            try {
                moshi.adapter(FreeWritingContent::class.java).fromJson(list[index].contentJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeListeningContent = _currentExercises.combine(_currentExerciseIndex) { list, index ->
        if (list.isNotEmpty() && index < list.size && list[index].exerciseType == "listening") {
            try {
                val listeningData = moshi.adapter(ListeningContent::class.java).fromJson(list[index].contentJson)
                listeningData
            } catch (e: Exception) {
                null
            }
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Actions
    fun startLesson(lessonId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            // Try Supabase first, fall back to Room
            SupabaseClient.fetchExercisesForLesson(lessonId) { remoteExercises ->
                viewModelScope.launch {
                    val fetched = if (!remoteExercises.isNullOrEmpty()) {
                        // Cache to Room for offline
                        dao.insertExercises(remoteExercises)
                        remoteExercises
                    } else {
                        // Fallback to Room cache
                        repository.getExercisesForLessonDirect(lessonId)
                    }
                    _currentExercises.value = fetched
                    _currentExerciseIndex.value = 0
                    _selectedOptionIndex.value = null
                    _selectedWordChips.value = emptyList()
                    _remainingWordBank.value = emptyList()
                    _textAnswer.value = ""
                    _isAnswerChecked.value = false
                    _isAnswerCorrect.value = false
                    _showFeedbackBar.value = false
                    _xpGainedInSession.value = 0
                    _gemsGainedInSession.value = 0
                    _isLessonFinished.value = false

                    val currentHearts = heartsState.value?.hearts_count ?: 5
                    _heartsLocal.value = currentHearts

                    if (fetched.isNotEmpty()) {
                        setupExerciseBanks(fetched[0])
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    private fun setupExerciseBanks(exercise: ExerciseEntity) {
        viewModelScope.launch {
            _selectedWordChips.value = emptyList()
            _textAnswer.value = ""
            _selectedOptionIndex.value = null

            try {
                when (exercise.exerciseType) {
                    "fill_blank" -> {
                        val content = moshi.adapter(FillBlankContent::class.java).fromJson(exercise.contentJson)
                        _remainingWordBank.value = content?.word_bank ?: emptyList()
                    }
                    "reorder" -> {
                        val content = moshi.adapter(ReorderContent::class.java).fromJson(exercise.contentJson)
                        _remainingWordBank.value = content?.shuffled_words ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                _remainingWordBank.value = emptyList()
            }
        }
    }

    fun selectOption(index: Int) {
        if (!_isAnswerChecked.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun selectWordChip(word: String) {
        if (!_isAnswerChecked.value) {
            _selectedWordChips.value = _selectedWordChips.value + word
            _remainingWordBank.value = _remainingWordBank.value.filterIndexed { i, s ->
                val firstMatchIndex = _remainingWordBank.value.indexOf(word)
                i != firstMatchIndex
            }
        }
    }

    fun deselectWordChip(word: String) {
        if (!_isAnswerChecked.value) {
            _remainingWordBank.value = _remainingWordBank.value + word
            _selectedWordChips.value = _selectedWordChips.value.filterIndexed { i, s ->
                val firstMatchIndex = _selectedWordChips.value.indexOf(word)
                i != firstMatchIndex
            }
        }
    }

    fun updateTextAnswer(text: String) {
        if (!_isAnswerChecked.value) {
            _textAnswer.value = text
        }
    }

    fun checkAnswer() {
        val exercisesList = _currentExercises.value
        val index = _currentExerciseIndex.value
        if (exercisesList.isEmpty() || index >= exercisesList.size) return

        val currentExercise = exercisesList[index]
        var correct = false
        var answerTextLogged = ""

        when (currentExercise.exerciseType) {
            "mcq" -> {
                val selected = _selectedOptionIndex.value
                val mcq = activeMcqContent.value
                if (selected != null && mcq != null) {
                    correct = (selected == mcq.correct_index)
                    answerTextLogged = mcq.options.getOrNull(selected) ?: ""
                }
            }
            "fill_blank" -> {
                val selectedWord = _selectedWordChips.value.firstOrNull() ?: ""
                correct = (selectedWord.equals(currentExercise.correctAnswer, ignoreCase = true))
                answerTextLogged = selectedWord
            }
            "reorder" -> {
                val compiledSentence = _selectedWordChips.value.joinToString(" ").trim()
                val targetSentence = currentExercise.correctAnswer?.trim() ?: ""
                
                val cleanCompiled = cleanString(compiledSentence)
                val cleanTarget = cleanString(targetSentence)
                
                correct = (cleanCompiled == cleanTarget)
                answerTextLogged = compiledSentence
            }
            "transform" -> {
                val typed = _textAnswer.value.trim()
                val target = currentExercise.correctAnswer?.trim() ?: ""
                
                val cleanTyped = cleanString(typed)
                val cleanTarget = cleanString(target)
                
                correct = (cleanTyped == cleanTarget)
                answerTextLogged = typed
            }
            "free_writing" -> {
                val text = _textAnswer.value.trim()
                val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
                val count = words.size
                val minWords = activeFreeWritingContent.value?.min_words ?: 20
                correct = count >= minWords
                answerTextLogged = text

                viewModelScope.launch {
                    repository.insertWritingSubmission(
                        WritingSubmissionEntity(
                            exerciseId = currentExercise.id,
                            topicAr = activeFreeWritingContent.value?.topic_ar ?: "كتابة حرة",
                            userText = text,
                            wordCount = count,
                            status = "Awaiting AI feedback"
                        )
                    )
                }
            }
        }

        _isAnswerCorrect.value = correct
        _isAnswerChecked.value = true
        _showFeedbackBar.value = true

        if (correct) {
            _xpGainedInSession.value += 10
            _gemsGainedInSession.value += 5
            val userId = SupabaseClient.getCurrentUserId()
            if (userId != null) {
                SupabaseClient.insertXpEvent(userId, 10, "exercise_correct") { _ -> }
                SupabaseClient.insertGemTransaction(userId, 5, "exercise_correct") { _ -> }
            }
        } else {
            if (currentExercise.exerciseType != "free_writing") {
                val updatedHearts = (_heartsLocal.value - 1).coerceAtLeast(0)
                _heartsLocal.value = updatedHearts

                viewModelScope.launch {
                    repository.insertWrongAnswer(
                        WrongAnswerEntity(
                            exerciseId = currentExercise.id,
                            userAnswer = answerTextLogged,
                            correctAnswer = currentExercise.correctAnswer ?: "",
                            skillTags = currentExercise.skillTags
                        )
                    )
                }
                trackWeakPoint(currentExercise.skillTags, false)
            }
        }
    }

    private fun cleanString(input: String): String {
        return input.lowercase()
            .replace("[.,?!'\"#]".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    fun continueLesson() {
        val exercisesList = _currentExercises.value
        val nextIndex = _currentExerciseIndex.value + 1

        _showFeedbackBar.value = false
        _isAnswerChecked.value = false

        if (nextIndex < exercisesList.size && _heartsLocal.value > 0) {
            _currentExerciseIndex.value = nextIndex
            setupExerciseBanks(exercisesList[nextIndex])
        } else {
            finishLesson()
        }
    }

    private fun finishLesson() {
        _isLessonFinished.value = true
        viewModelScope.launch {
            val userId = SupabaseClient.getCurrentUserId()
            val totalXpGained = _xpGainedInSession.value
            val totalGemsGained = _gemsGainedInSession.value

            if (userId != null) {
                // Log XP event
                if (totalXpGained > 0) {
                    SupabaseClient.insertXpEvent(userId, totalXpGained, "lesson_complete") { _ -> }
                }
                // Log gem transaction
                if (totalGemsGained > 0) {
                    SupabaseClient.insertGemTransaction(userId, totalGemsGained, "lesson_complete") { _ -> }
                }
            }

            // Update local Room progress for backward compat
            val currentProgress = repository.getUserProgress() ?: UserProgressEntity(userId = userId ?: "")
            val updatedProgress = currentProgress.copy(
                hearts = _heartsLocal.value,
                gems = currentProgress.gems + totalGemsGained,
                xp = currentProgress.xp + totalXpGained
            )
            repository.updateUserProgress(updatedProgress)

            // Refresh user state from Supabase
            fetchHeartsState()
            fetchGemWallet()
            fetchUserStreaksData()
            fetchUserLevelsData()
        }
    }

    fun rechargeHearts() {
        viewModelScope.launch {
            val wallet = gemWallet.value ?: return@launch
            if (wallet.balance >= 350) {
                val userId = SupabaseClient.getCurrentUserId() ?: return@launch
                SupabaseClient.insertGemTransaction(userId, -350, "hearts_recharge") { _ -> }
                _gemWallet.value = wallet.copy(balance = wallet.balance - 350)
                _heartsLocal.value = 5
            }
        }
    }

    fun buyStreakFreeze() {
        viewModelScope.launch {
            val wallet = gemWallet.value ?: return@launch
            val freeze = streakFreeze.value
            if (wallet.balance >= 200 && (freeze?.quantity ?: 0) < 2) {
                val userId = SupabaseClient.getCurrentUserId() ?: return@launch
                SupabaseClient.insertGemTransaction(userId, -200, "streak_freeze") { _ -> }
                _gemWallet.value = wallet.copy(balance = wallet.balance - 200)
                fetchStreakFreezeData()
            }
        }
    }

    fun useStreakFreezeIfNeeded(streakWasToday: Boolean) {
        if (streakWasToday) return
        val freeze = streakFreeze.value ?: return
        if (freeze.quantity <= 0) return
        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            val updatedFreeze = freeze.copy(quantity = freeze.quantity - 1)
            val json = """{"user_id":"${SupabaseClient.jsonEscapeExternal(userId)}","quantity":${updatedFreeze.quantity}}"""
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
            val headers = SupabaseClient.getAuthHeadersExternal()
            val requestBuilder = okhttp3.Request.Builder()
                .url("${BuildConfig.SUPABASE_URL}/rest/v1/streak_freeze_inventory?user_id=eq.${SupabaseClient.jsonEscapeExternal(userId)}")
                .patch(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
            headers.forEach { pair -> requestBuilder.addHeader(pair.first, pair.second) }
            SupabaseClient.getHttpClientExternal().newCall(requestBuilder.build()).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {}
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) { response.close() }
            })
            _streakFreeze.value = updatedFreeze
            SupabaseClient.insertGemTransaction(userId, 0, "streak_freeze_used") { _ -> }
        }
    }

    fun updateUserSettings(sound: Boolean, anim: Boolean, notif: Boolean) {
        viewModelScope.launch {
            val progress = repository.getUserProgress() ?: return@launch
            val updated = progress.copy(
                soundEffectsEnabled = sound,
                animationsEnabled = anim,
                notificationsEnabled = notif
            )
            repository.updateUserProgress(updated)
        }
    }

    fun updateUserProfile(username: String, email: String) {
        viewModelScope.launch {
            val progress = repository.getUserProgress() ?: return@launch
            val updated = progress.copy(
                username = username,
                email = email
            )
            repository.updateUserProgress(updated)
        }
    }

    fun resetProgress() {
        val userId = SupabaseClient.getCurrentUserId() ?: return
        viewModelScope.launch {
            val base = UserProgressEntity(
                userId = userId,
                hearts = 5
            )
            repository.updateUserProgress(base)
            repository.clearWrongAnswers()
            startLesson(1)
        }
    }

    // Auth methods
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            SupabaseClient.signInWithEmail(email, password) { result ->
                viewModelScope.launch {
                    result.onSuccess { token ->
                        _isAuthenticated.value = true
                        _uiState.value = UiState.Success("تم تسجيل الدخول بنجاح")
                        syncRemoteData()
                    }.onFailure { error ->
                        _errorMessage.value = error.message ?: "Failed to sign in"
                        _uiState.value = UiState.Error(error.message ?: "Failed to sign in")
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            SupabaseClient.signUpWithEmail(email, password) { result ->
                viewModelScope.launch {
                    result.onSuccess { token ->
                        _isAuthenticated.value = true
                        _uiState.value = UiState.Success("تم إنشاء الحساب بنجاح")
                    }.onFailure { error ->
                        _errorMessage.value = error.message ?: "Failed to sign up"
                        _uiState.value = UiState.Error(error.message ?: "Failed to sign up")
                    }
                    _isLoading.value = false
                }
            }
        }
    }

    fun signOut() {
        SupabaseClient.signOut()
        _isAuthenticated.value = false
        _uiState.value = UiState.Idle
    }

    fun retrySync() {
        syncRemoteData()
    }

    fun clearError() {
        _errorMessage.value = null
        _uiState.value = UiState.Idle
    }

    var showDeleteConfirmDialog = mutableStateOf(false)

    fun confirmDeleteAccount() {
        showDeleteConfirmDialog.value = true
    }

    fun dismissDeleteConfirmDialog() {
        showDeleteConfirmDialog.value = false
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            showDeleteConfirmDialog.value = false
            SupabaseClient.deleteAccount { success, error ->
                if (success) {
                    _isAuthenticated.value = false
                    _uiState.value = UiState.Idle
                } else {
                    _errorMessage.value = error ?: "حدث خطأ أثناء حذف الحساب"
                    _uiState.value = UiState.Error(error ?: "Unknown error")
                }
            }
        }
    }
}
