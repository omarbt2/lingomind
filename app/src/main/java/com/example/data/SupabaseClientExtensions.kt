package com.example.data

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Suspend wrappers for SupabaseClient
suspend fun SupabaseClient.signInWithEmailSuspend(email: String, password: String): Result<String> = suspendCoroutine { cont ->
    signInWithEmail(email, password) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.signUpWithEmailSuspend(email: String, password: String): Result<String> = suspendCoroutine { cont ->
    signUpWithEmail(email, password) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.resetPasswordSuspend(email: String): Result<Unit> = suspendCoroutine { cont ->
    resetPassword(email) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.saveInterestsSuspend(
    accessToken: String,
    interests: List<String>
): Result<Unit> = suspendCoroutine { cont ->
    saveInterests(accessToken, interests) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.saveLevelSuspend(
    accessToken: String,
    level: String
): Result<Unit> = suspendCoroutine { cont ->
    saveLevel(accessToken, level) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.insertMilestoneAttemptSuspend(
    userId: String,
    testId: Int,
    score: Int,
    passed: Boolean
): Result<Unit> = suspendCoroutine { cont ->
    insertMilestoneAttempt(userId, testId, score, passed) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.upsertPhaseUnlockSuspend(
    userId: String,
    phaseId: Int
): Result<Unit> = suspendCoroutine { cont ->
    upsertPhaseUnlock(userId, phaseId) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.updateUserSettingsSuspend(
    userId: String,
    settings: UserSettingsEntity
): Result<Unit> = suspendCoroutine { cont ->
    updateUserSettings(userId, settings) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.addBookmarkSuspend(
    userId: String,
    lessonId: Int
): Result<Unit> = suspendCoroutine { cont ->
    addBookmark(userId, lessonId) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.removeBookmarkSuspend(
    userId: String,
    lessonId: Int
): Result<Unit> = suspendCoroutine { cont ->
    removeBookmark(userId, lessonId) { result ->
        cont.resume(result)
    }
}

suspend fun SupabaseClient.upsertWeakPointSuspend(
    userId: String,
    skillTag: String,
    errorCount: Int,
    accuracyPct: Float
): Result<Unit> = suspendCoroutine { cont ->
    upsertWeakPoint(userId, skillTag, errorCount, accuracyPct) { result ->
        cont.resume(result)
    }
}
