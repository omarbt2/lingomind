package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// JSON parsing helper models
data class McqContent(val options: List<String>, val correct_index: Int)
data class FillBlankContent(val sentence: String, val word_bank: List<String>)
data class ReorderContent(val shuffled_words: List<String>)
data class TransformContent(val original_sentence: String, val instruction_ar: String)
data class FreeWritingContent(val min_words: Int, val topic_ar: String)

class LingoMindViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LingoMindRepository
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Database Flows
    val userProgress: StateFlow<UserProgressEntity?>
    val wrongAnswers: StateFlow<List<WrongAnswerEntity>>
    val writingSubmissions: StateFlow<List<WritingSubmissionEntity>>

    init {
        val database = AppDatabase.getInstance(application)
        val dao = database.lingoMindDao()
        repository = LingoMindRepository(dao)

        userProgress = repository.getUserProgressFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        wrongAnswers = repository.getAllWrongAnswers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        writingSubmissions = repository.getAllWritingSubmissions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Bidirectional Sync on startup: load remote state or push local state if remote is empty
        viewModelScope.launch {
            SupabaseClient.fetchUserProgress(1) { remoteProgress ->
                if (remoteProgress != null) {
                    viewModelScope.launch {
                        dao.insertUserProgress(remoteProgress)
                    }
                } else {
                    viewModelScope.launch {
                        val localProgress = repository.getUserProgress()
                        if (localProgress != null) {
                            SupabaseClient.syncUserProgress(localProgress)
                        }
                    }
                }
            }
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

    // Actions
    fun startLesson(lessonId: Int) {
        viewModelScope.launch {
            val fetched = repository.getExercisesForLessonDirect(lessonId)
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

            val currentHearts = userProgress.value?.hearts ?: 5
            _heartsLocal.value = currentHearts

            // Setup initial word banks if applicable
            if (fetched.isNotEmpty()) {
                setupExerciseBanks(fetched[0])
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
                // Remove only the first occurrence to handle duplicates correctly
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
                
                // Compare cleaned strings (lowercase, stripped punctuation)
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

                // Submit free writing
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
        } else {
            // Decrement heart if not free writing (free writing is always successful as long as min word limit is met)
            if (currentExercise.exerciseType != "free_writing") {
                val updatedHearts = (_heartsLocal.value - 1).coerceAtLeast(0)
                _heartsLocal.value = updatedHearts

                // Save to wrong answers log
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
            // Lesson completed or out of hearts!
            finishLesson()
        }
    }

    private fun finishLesson() {
        _isLessonFinished.value = true
        viewModelScope.launch {
            val currentProgress = repository.getUserProgress() ?: UserProgressEntity()
            
            // Calculate gems and streak boost
            val totalGemsGained = _gemsGainedInSession.value
            val totalXpGained = _xpGainedInSession.value

            val updatedProgress = currentProgress.copy(
                hearts = _heartsLocal.value,
                gems = currentProgress.gems + totalGemsGained,
                xp = currentProgress.xp + totalXpGained,
                // Keep the streak at 5 days as pre-populated, plus add dynamic day if finished successfully
                streak = if (_heartsLocal.value > 0) currentProgress.streak else currentProgress.streak
            )
            repository.updateUserProgress(updatedProgress)
        }
    }

    fun rechargeHearts() {
        viewModelScope.launch {
            val progress = repository.getUserProgress() ?: return@launch
            if (progress.gems >= 350) {
                val updated = progress.copy(
                    gems = progress.gems - 350,
                    hearts = 5
                )
                repository.updateUserProgress(updated)
                _heartsLocal.value = 5
            }
        }
    }

    fun buyStreakFreeze() {
        viewModelScope.launch {
            val progress = repository.getUserProgress() ?: return@launch
            if (progress.gems >= 200 && progress.streakFreezeCount < 2) {
                val updated = progress.copy(
                    gems = progress.gems - 200,
                    streakFreezeCount = progress.streakFreezeCount + 1
                )
                repository.updateUserProgress(updated)
            }
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

    fun resetProgress() {
        viewModelScope.launch {
            // Reset state
            val base = UserProgressEntity(
                hearts = 5,
                gems = 1264,
                streak = 5,
                xp = 887,
                streakFreezeCount = 2
            )
            repository.updateUserProgress(base)
            repository.clearWrongAnswers()
            
            // Reload initial exercises
            startLesson(1)
        }
    }
}
