package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FriendEntity
import com.example.data.UserProgressEntity
import com.example.ui.semaViewModel
import com.example.ui.theme.semaIcons
import com.example.ui.theme.PrimaryBlue
import java.time.LocalDate
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor
import com.example.ui.screens.FlashcardDeck
import com.example.ui.screens.FlashcardItem
import com.example.ui.screens.Competitor
import com.example.ui.screens.AchievementType
import com.example.ui.screens.SupportCategory
import com.example.ui.screens.FaqItem
import com.example.ui.screens.Friend

@Composable
fun FriendsScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val searchQuery = remember { mutableStateOf("") }

    // Fetch friends when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchFriends()
    }

    // Convert FriendEntity to Friend for the UI
    // Note: The FriendEntity doesn't include profiles data from the join
    // We'll use friendId as identifier
    val uiFriends = remember(friends) {
        friends.map { entity ->
            Friend(
                id = entity.id,
                username = "User ${entity.friendId.take(8)}",
                xp = 0,
                isOnline = false,
                isFollowing = entity.status == "accepted",
                isFollowedBy = entity.status == "accepted"
            )
        }
    }

    FriendsScreen(
        friends = uiFriends,
        searchQuery = searchQuery.value,
        onSearchQueryChange = { searchQuery.value = it },
        onInviteClick = { /* TODO: Implement invite */ },
        onBack = onBack
    )
}

@Composable
fun AchievementsListScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val userAchievements by viewModel.userAchievements.collectAsStateWithLifecycle()

    // Fetch data when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchAchievementTypes()
        viewModel.fetchUserAchievements()
    }

    val achievementList = achievements.map { achievement ->
        val isUnlocked = userAchievements.any { it.achievementId == achievement.id }
        val progress = when {
            achievement.conditionJson.contains("streak") -> userProgress?.streak ?: 0
            achievement.conditionJson.contains("xp") -> userProgress?.xp ?: 0
            achievement.conditionJson.contains("gems") -> userProgress?.gems ?: 0
            achievement.conditionJson.contains("league") -> if (userProgress?.league == "الذهبي") 1 else 0
            achievement.conditionJson.contains("writing") -> 0
            achievement.conditionJson.contains("lessons") -> 0
            else -> 0
        }
        val target = when {
            achievement.conditionJson.contains("streak") -> 10
            achievement.conditionJson.contains("xp") -> 1000
            achievement.conditionJson.contains("gems") -> 500
            achievement.conditionJson.contains("league") -> 1
            achievement.conditionJson.contains("writing") -> 10
            achievement.conditionJson.contains("lessons") -> 50
            else -> 1
        }
        val icon = when (achievement.iconName) {
            "LocalFireDepartment" -> semaIcons.localFireDepartment()
            "WorkspacePremium" -> semaIcons.workspacePremium()
            "Diamond" -> semaIcons.diamond()
            "EmojiEvents" -> semaIcons.emojiEvents()
            "Translate" -> semaIcons.translate()
            "CheckCircle" -> semaIcons.checkCircle()
            else -> semaIcons.localFireDepartment()
        }
        AchievementType(
            id = achievement.id.toIntOrNull() ?: 0,
            name = achievement.nameAr,
            description = achievement.descriptionAr,
            icon = icon,
            color = ComposeColor(AndroidColor.parseColor(achievement.colorHex)),
            target = target,
            current = progress.coerceAtMost(target)
        )
    }

    AchievementsListScreen(
        achievements = achievementList,
        onBack = onBack
    )
}

@Composable
fun EditProfileScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()

    userProgress?.let { progress ->
        EditProfileScreen(
            initialUsername = progress.username,
            initialEmail = progress.email,
            onSave = { username, email ->
                viewModel.updateUserProfile(username, email)
            },
        onBack = onBack
    )
}

@Composable
fun AboutScreenWrapper(
    onBack: () -> Unit
) {
    AboutScreen(
        onBack = onBack
    )
}
}

@Composable
fun FlashcardDeckOverviewScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val flashcards by viewModel.flashcards.collectAsStateWithLifecycle()

    // Fetch flashcards when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchFlashcards()
    }

    // Group flashcards by lesson to create decks
    val decks = remember(flashcards) {
        flashcards.groupBy { it.lessonId }.map { (lessonId, cards) ->
            val firstCard = cards.first()
            FlashcardDeck(
                id = lessonId,
                name = firstCard.deckName,
                totalCards = cards.size,
                masteredCards = 0,
                category = firstCard.deckName
            )
        }
    }

    FlashcardDeckOverviewScreen(
        decks = decks,
        onDeckClick = { deck ->
            // Navigation handled by MainActivity
        },
        onBack = onBack
    )
}

@Composable
fun FlashcardReviewScreenWrapper(
    viewModel: semaViewModel,
    deckId: Int,
    onBack: () -> Unit
) {
    val flashcards by viewModel.flashcards.collectAsStateWithLifecycle()

    // Filter flashcards for this deck (lesson)
    val deckFlashcards = remember(flashcards, deckId) {
        flashcards.filter { it.lessonId == deckId }.map { card ->
            FlashcardItem(
                id = card.id,
                front = card.frontText,
                back = card.backText,
                deckName = card.deckName
            )
        }
    }

    val deckName = remember(deckId) {
        flashcards.firstOrNull { it.lessonId == deckId }?.deckName ?: "رصيد البطاقات"
    }

    FlashcardReviewScreen(
        deckName = deckName,
        flashcards = deckFlashcards,
        onFinished = {
            // Update review schedule via viewModel
        },
        onBack = onBack
    )
}

@Composable
fun StreakCalendarScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val xpEvents by viewModel.xpEvents.collectAsStateWithLifecycle()
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()

    // Fetch XP events when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchXPEvents()
    }

    // Create a set of active days from xp_events (convert Long timestamps to LocalDate)
    val activeDays = remember(xpEvents) {
        xpEvents.map { LocalDate.ofEpochDay(it.createdAt / 86400000) }.toSet()
    }

    val currentStreak = userProgress?.streak ?: 0

    StreakCalendarScreen(
        currentStreak = currentStreak,
        activeDays = activeDays,
        onBack = onBack
    )
}

@Composable
fun SupportScreenWrapper(
    onBack: () -> Unit
) {
    // Support screen uses static data - no ViewModel needed
    val categories = listOf(
        SupportCategory(
            title = "الحساب والملف الشخصي",
            icon = semaIcons.person(),
            items = listOf(
                FaqItem("كيف أغير اسم المستخدم؟", "انتقل إلى الملف الشخصي > تعديل الملف الشخصي > غيّر الاسم > احفظ"),
                FaqItem("كيف أغيّر البريد الإلكتروني؟", "من تعديل الملف الشخصي، حدّث البريد الإلكتروني واحفظ التغييرات"),
                FaqItem("نسيت كلمة المرور", "في شاشة تسجيل الدخول، اضغط 'نسيت كلمة المرور' وأدخل بريدك الإلكتروني"),
            )
        ),
        SupportCategory(
            title = "السلسلة والجوائز",
            icon = semaIcons.localFireDepartment(),
            items = listOf(
                FaqItem("ما هي السلسلة؟", "عدد الأيام المتتالية التي أكملت فيها درساً واحداً على الأقل"),
                FaqItem("ماذا يحدث إذا انقطعت السلسلة؟", "تعود إلى الصفر، لكن يمكنك استخدام 'مجمد السلسلة' فيحميها ليوم واحد"),
                FaqItem("كيف أحصل على المزيد من الجواهر؟", "بإكمال الدروس، تحقيق الإنجازات، والصعود في الدوري"),
            )
        ),
        SupportCategory(
            title = "المتجر والمشتريات",
            icon = semaIcons.shoppingCart(),
            items = listOf(
                FaqItem("كيف أشتري قلوباً إضافية؟", "في المتجر، اختر 'إعادة شحن القلوب' مقابل 350 جوهرة"),
                FaqItem("ما هو مجمد السلسلة؟", "يحمي سلسلتك ليوم واحد إذا فاتك درس، تشتريه من المتجر بـ 200 جوهرة"),
                FaqItem("المشتريات لم تظهر", "أعد تشغيل التطبيق، إذا استمرت المشكلة تواصل مع الدعم"),
            )
        ),
        SupportCategory(
            title = "مشاكل تقنية",
            icon = semaIcons.settings(),
            items = listOf(
                FaqItem("التطبيق لا يفتح", "أعد تشغيل الجهاز، تأكد من تحديث التطبيق، امسح بيانات التطبيق إن لزم"),
                FaqItem("الصوت لا يعمل", "تحقق من إعدادات الصوت في التطبيق وإعدادات الجهاز"),
                FaqItem("التزامن لا يعمل", "تأكد من الاتصال بالإنترنت، اضغط 'إعادة المحاولة' في شاشة الخطأ"),
            )
        ),
    )

    SupportScreen(
        categories = categories,
        onBack = onBack
    )
}

@Composable
fun MilestoneTestScreenWrapper(
    viewModel: semaViewModel,
    phaseId: Int,
    onBack: () -> Unit,
    onNavigateToResult: (Int, Int, Boolean) -> Unit = { _, _, _ -> }
) {
    LaunchedEffect(phaseId) {
        viewModel.fetchMilestoneTest(phaseId)
    }
    MilestoneTestScreen(
        viewModel = viewModel,
        onBackToHome = onBack,
        onNavigateToResult = onNavigateToResult
    )
}

@Composable
fun LeaguesScreenWrapper(
    viewModel: semaViewModel,
    onStartLesson: () -> Unit,
    onBack: () -> Unit
) {
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Fetch leaderboard when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchWeeklyLeaderboard()
    }

    val currentUserId = com.example.data.SupabaseClient.getCurrentUserId() ?: ""
    val currentXp = userProgress?.xp ?: 0

    val competitors = leaderboard.map { entry ->
        Competitor(
            username = "مستخدم ${entry.userId.take(8)}",
            xp = entry.xpEarned,
            avatarIcon = semaIcons.accountCircle(),
            avatarTint = PrimaryBlue,
            isCurrentUser = entry.userId == currentUserId
        )
    }.sortedByDescending { it.xp }

    LeaguesScreen(
        competitors = competitors,
        onStartLesson = onStartLesson,
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun QuestsScreenWrapper(
    viewModel: semaViewModel,
    onAuthClick: () -> Unit,
    onBack: () -> Unit
) {
    val todaysChallenge by viewModel.todaysChallenge.collectAsStateWithLifecycle()
    val userDailyChallenge by viewModel.userDailyChallenge.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Fetch challenge data when screen is first shown
    LaunchedEffect(Unit) {
        viewModel.fetchTodaysChallenge()
        viewModel.fetchUserDailyChallenge()
    }

    val userProgress = viewModel.userProgress.collectAsStateWithLifecycle()
    val currentXp = userProgress.value?.xp ?: 0

    QuestsScreen(
        viewModel = viewModel,
        todaysChallenge = todaysChallenge,
        userDailyChallenge = userDailyChallenge,
        onAuthClick = onAuthClick,
        onBack = onBack,
        isLoading = isLoading
    )
}

@Composable
fun ShopScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val userProgress = viewModel.userProgress.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage = viewModel.errorMessage.collectAsStateWithLifecycle()

    ShopScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun PronunciationScreenWrapper(
    viewModel: semaViewModel,
    lessonId: Int,
    onBack: () -> Unit
) {
    PronunciationPracticeScreen(
        viewModel = viewModel,
        lessonId = lessonId,
        onBack = onBack
    )
}

@Composable
fun ListeningExerciseScreenWrapper(
    viewModel: semaViewModel,
    lessonId: Int,
    onBack: () -> Unit
) {
    ListeningExerciseScreen(
        viewModel = viewModel,
        lessonId = lessonId,
        onBack = onBack
    )
}

@Composable
fun AIChatScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    AIChatScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun WritingPracticeScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    WritingPracticeScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun NotificationSettingsScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    NotificationSettingsScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun DailyChallengesScreenWrapper(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    DailyChallengesScreen(
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun MilestoneResultScreenWrapper(
    viewModel: semaViewModel,
    phaseId: Int,
    score: Int,
    passed: Boolean,
    onBack: () -> Unit
) {
    MilestoneResultScreen(
        viewModel = viewModel,
        phaseId = phaseId,
        score = score,
        passed = passed,
        onRetry = {
            viewModel.resetMilestoneTest()
            viewModel.fetchMilestoneTest(phaseId)
        },
        onContinue = {
            viewModel.resetMilestoneTest()
            onBack()
        },
        onBack = onBack
    )
}

@Composable
fun AboutScreenWrapper(
    onBack: () -> Unit
) {
    AboutScreen(
        onBack = onBack
    )
}
