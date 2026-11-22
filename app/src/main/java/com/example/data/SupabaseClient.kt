package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_KEY = BuildConfig.SUPABASE_ANON_KEY
    private const val PREFS_NAME = "sema_supabase"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_SESSION_EXPIRY = "session_expiry"
    private const val KEY_USER_ID = "user_id"

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    fun getHttpClientExternal(): OkHttpClient = client
    fun jsonEscapeExternal(value: String): String = jsonEscape(value)

    private var prefs: SharedPreferences? = null
    private var initialized = false

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        initialized = true
    }

    fun isInitialized(): Boolean = initialized

    private fun getAccessTokenInternal(): String? {
        return prefs?.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getAccessToken(): String? = getAccessTokenInternal()

    fun getCurrentUserId(): String? = prefs?.getString(KEY_USER_ID, null)

    private fun saveUserId(userId: String) {
        prefs?.edit()?.putString(KEY_USER_ID, userId)?.apply()
    }

    private fun clearUserId() {
        prefs?.edit()?.remove(KEY_USER_ID)?.apply()
    }

    private fun saveSession(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiry = System.currentTimeMillis() + (expiresIn * 1000)
        prefs?.edit()?.apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_SESSION_EXPIRY, expiry)
            apply()
        }
    }

    private fun clearSession() {
        prefs?.edit()?.apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_SESSION_EXPIRY)
            apply()
        }
        clearUserId()
    }

    // 60s safety margin so we refresh BEFORE Supabase rejects the token, not after.
    private const val EXPIRY_MARGIN_MS = 60_000L

    private fun isSessionValid(): Boolean {
        val expiry = prefs?.getLong(KEY_SESSION_EXPIRY, 0) ?: 0
        return System.currentTimeMillis() < (expiry - EXPIRY_MARGIN_MS)
    }

    fun getAuthHeadersExternal(): List<Pair<String, String>> {
        return getAuthHeaders()
    }

    // FIX #1: Previously, once the access token expired (~1h), every subsequent request
    // silently fell back to the anon key. RLS policies like `auth.uid() = user_id` then
    // reject the request, but the HTTP call still "succeeds" from OkHttp's point of view
    // (no exception), so the app never surfaces an error — the user just stops seeing
    // their data ("البيانات ماكتظهرش بعد شوية"). We now synchronously refresh the token
    // using the stored refresh_token BEFORE building auth headers, whenever the access
    // token is expired/near-expiry. This blocking call is safe here because getAuthHeaders()
    // is only ever invoked from background/network callback threads (OkHttp dispatcher),
    // never from the main thread.
    private fun getAuthHeaders(): List<Pair<String, String>> {
        var token = getAccessTokenInternal()
        if (token != null && !isSessionValid()) {
            token = refreshAccessTokenBlocking()
        }
        return if (token != null) {
            listOf(
                "apikey" to SUPABASE_KEY,
                "Authorization" to "Bearer $token"
            )
        } else {
            // No valid session at all (never logged in / refresh failed) -> anon key.
            // RLS will correctly reject any per-user write/read in this case, which is
            // the desired behavior (as opposed to silently succeeding with stale data).
            listOf(
                "apikey" to SUPABASE_KEY,
                "Authorization" to "Bearer $SUPABASE_KEY"
            )
        }
    }

    // Synchronous refresh-token grant. Returns the new access token, or null if the
    // refresh token is missing/invalid (in which case the session is cleared and the
    // caller should treat the user as logged out).
    private fun refreshAccessTokenBlocking(): String? {
        val refreshToken = prefs?.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val json = """{"refresh_token":"${jsonEscape(refreshToken)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$SUPABASE_URL/auth/v1/token?grant_type=refresh_token")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Content-Type", "application/json")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseStr = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val adapter = moshi.adapter(AuthResponse::class.java)
                    val authResponse = adapter.fromJson(responseStr)
                    if (authResponse != null) {
                        saveSession(authResponse.access_token, authResponse.refresh_token, authResponse.expires_in)
                        authResponse.access_token
                    } else null
                } else {
                    Log.e(TAG, "Token refresh failed: ${response.code} - $responseStr")
                    // Refresh token itself is invalid/expired -> force logout so the UI
                    // can route the user back to Sign In instead of silently failing forever.
                    clearSession()
                    null
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Token refresh network error: ${e.message}")
            null // transient network error: keep the (stale) session, try again next call
        }
    }

    // FIX #7 (partial): minimal JSON string escaping for values interpolated into the
    // hand-built request bodies used throughout this file. This does not replace properly
    // typed request bodies (see MASTER PROMPT item on migrating to kotlinx.serialization),
    // but it closes the immediate injection risk where a value containing a `"` or `\`
    // (e.g. a username, an email, free-text) could break out of the JSON string and alter
    // the request structure.
    private fun jsonEscape(value: String): String {
        val sb = StringBuilder(value.length + 8)
        for (c in value) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    // Sign in with email and password
    fun signInWithEmail(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit
    ) {
        val json = """{"email":"${jsonEscape(email)}","password":"${jsonEscape(password)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/auth/v1/token?grant_type=password")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to sign in: ${e.message}")
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    try {
                        val adapter = moshi.adapter(AuthResponse::class.java)
                        val authResponse = adapter.fromJson(responseStr)
                        if (authResponse != null) {
                            saveSession(
                                authResponse.access_token,
                                authResponse.refresh_token,
                                authResponse.expires_in
                            )
                            saveUserId(authResponse.user?.id ?: "")
                            callback(Result.success(authResponse.access_token))
                        } else {
                            callback(Result.failure(Exception("Invalid response")))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing auth response: ${e.message}")
                        callback(Result.failure(e))
                    }
                } else {
                    Log.e(TAG, "Failed to sign in: ${response.code} - $responseStr")
                    callback(Result.failure(Exception("Sign in failed: ${response.code}")))
                }
                response.close()
            }
        })
    }

    // Sign up with email and password
    fun signUpWithEmail(
        email: String,
        password: String,
        callback: (Result<String>) -> Unit
    ) {
        val json = """{"email":"${jsonEscape(email)}","password":"${jsonEscape(password)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/auth/v1/signup")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to sign up: ${e.message}")
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    try {
                        val adapter = moshi.adapter(AuthResponse::class.java)
                        val authResponse = adapter.fromJson(responseStr)
                        if (authResponse != null) {
                            saveSession(
                                authResponse.access_token,
                                authResponse.refresh_token,
                                authResponse.expires_in
                            )
                            saveUserId(authResponse.user?.id ?: "")
                            callback(Result.success(authResponse.access_token))
                        } else {
                            callback(Result.failure(Exception("Invalid response")))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing auth response: ${e.message}")
                        callback(Result.failure(e))
                    }
                } else {
                    Log.e(TAG, "Failed to sign up: ${response.code} - $responseStr")
                    callback(Result.failure(Exception("Sign up failed: ${response.code}")))
                }
                response.close()
            }
        })
    }

    // Sign out
    fun signOut() {
        clearSession()
    }

    // Delete account via Edge Function
    fun deleteAccount(callback: (Boolean, String?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/functions/v1/delete-account")
            .post("".toRequestBody(null))
            .addHeader("Content-Type", "application/json")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    clearSession()
                    callback(true, null)
                } else {
                    callback(false, response.body?.string() ?: "Unknown error")
                }
                response.close()
            }
        })
    }

    // Check if user is authenticated
    fun isAuthenticated(): Boolean {
        return isSessionValid()
    }

    // Sync User Progress with Supabase (Upsert, scoped to the authenticated user)
    fun syncUserProgress(progress: UserProgressEntity) {
        // FIX #8 (critical): the old code POSTed the whole entity — including its
        // hardcoded defaults — for EVERY update, relying on the local Room primary key
        // (which defaults to id=1 for every installation) to line up with Supabase's
        // conflict target. That meant: (a) a fresh install triggering any progress write
        // before login/hydration would push placeholder data, and (b) upserts were never
        // reliably scoped to `user_id`, so two different local rows with id=1 could
        // overwrite each other's Supabase row. We now (1) hard-refuse to sync without a
        // real authenticated user_id, and (2) explicitly upsert on the `user_id` column.
        val userId = progress.userId
        if (userId.isBlank()) {
            Log.e(TAG, "syncUserProgress aborted: no authenticated userId set on entity")
            return
        }

        val jsonAdapter = moshi.adapter(UserProgressEntity::class.java)
        val json = jsonAdapter.toJson(progress)

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()

        val requestBuilder = Request.Builder()
            // on_conflict=user_id requires a UNIQUE constraint on user_progress.user_id
            // (see accompanying SQL migration) so this always updates the caller's own row.
            .url("$SUPABASE_URL/rest/v1/user_progress?on_conflict=user_id")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates,return=minimal")

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to sync user progress: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully synced user progress to Supabase!")
                } else {
                    Log.e(TAG, "Failed response syncing user progress: ${response.code} - ${response.message}")
                }
                response.close()
            }
        })
    }

    // Fetch User Progress from Supabase (by user_id)
    fun fetchUserProgress(userId: String, callback: (UserProgressEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_progress?user_id=eq.$userId&select=*")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user progress: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserProgressEntity::class.java)
                        val adapter = moshi.adapter<List<UserProgressEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user progress from Supabase: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching user progress: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Sync Writing Submission with Supabase (Upsert)
    fun syncWritingSubmission(submission: WritingSubmissionEntity) {
        val jsonAdapter = moshi.adapter(WritingSubmissionEntity::class.java)
        val json = jsonAdapter.toJson(submission)

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()

        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/writing_submissions")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to sync writing submission: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully synced writing submission to Supabase!")
                } else {
                    Log.e(TAG, "Failed response syncing writing submission: ${response.code}")
                }
                response.close()
            }
        })
    }

    // Password reset email
    fun resetPassword(email: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"email":"${jsonEscape(email)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/auth/v1/recover")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Save interests to profiles table (called in onboarding Step 2)
    fun saveInterests(
        accessToken: String,
        interests: List<String>,
        callback: (Result<Unit>) -> Unit
    ) {
        val currentUserId = SupabaseClient.getCurrentUserId() ?: ""
        val json = """{"interests":${moshi.adapter(List::class.java).toJson(interests)}}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/profiles?user_id=eq.$currentUserId")
            .patch(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Save level to profiles table (called in onboarding Step 1) - uses POST with upsert to create profile if needed
    fun saveLevel(
        accessToken: String,
        level: String,
        callback: (Result<Unit>) -> Unit
    ) {
        val json = """{"level":"${jsonEscape(level)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/profiles")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Fetch friends list
    fun fetchFriends(userId: String, callback: (List<FriendEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/friends?select=*,profiles!friends_friend_id_fkey(username,avatar_url)&or=(user_id.eq.$userId,friend_id.eq.$userId)&status=eq.accepted")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch friends: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, FriendEntity::class.java)
                        val adapter = moshi.adapter<List<FriendEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing friends: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching friends: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Send friend request
    fun sendFriendRequest(userId: String, friendId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","friend_id":"${jsonEscape(friendId)}","status":"pending"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/friends")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer ${getAccessTokenInternal() ?: SUPABASE_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Accept/decline friend request
    fun respondToFriendRequest(requestId: Int, status: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"status":"${jsonEscape(status)}","responded_at":"${System.currentTimeMillis()}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/friends?id=eq.$requestId")
            .patch(requestBody)
            .addHeader("Content-Type", "application/json")

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Fetch achievement types
    fun fetchAchievementTypes(callback: (List<AchievementTypeEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/achievement_types?select=*")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch achievement types: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, AchievementTypeEntity::class.java)
                        val adapter = moshi.adapter<List<AchievementTypeEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing achievement types: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching achievement types: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Fetch user achievements
    fun fetchUserAchievements(userId: String, callback: (List<UserAchievementEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_achievements?user_id=eq.$userId&select=*")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user achievements: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserAchievementEntity::class.java)
                        val adapter = moshi.adapter<List<UserAchievementEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user achievements: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching user achievements: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Fetch weekly leaderboard
    fun fetchWeeklyLeaderboard(limit: Int, callback: (List<LeaderboardWeeklyEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/leaderboard_weekly?select=*,profiles(username,avatar_url)&order=xp_earned.desc&limit=$limit")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch leaderboard: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, LeaderboardWeeklyEntity::class.java)
                        val adapter = moshi.adapter<List<LeaderboardWeeklyEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing leaderboard: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching leaderboard: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Fetch today's daily challenge
    fun fetchTodaysChallenge(date: String, callback: (DailyChallengeEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/daily_challenges?date=eq.$date&select=*")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch today's challenge: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, DailyChallengeEntity::class.java)
                        val adapter = moshi.adapter<List<DailyChallengeEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing daily challenge: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching daily challenge: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Fetch user's status for a daily challenge
    fun fetchUserDailyChallenge(userId: String, challengeId: Int, callback: (UserDailyChallengeEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_daily_challenges?user_id=eq.$userId&challenge_id=eq.$challengeId&select=*")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user daily challenge: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserDailyChallengeEntity::class.java)
                        val adapter = moshi.adapter<List<UserDailyChallengeEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user daily challenge: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching user daily challenge: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Complete daily challenge
    fun completeDailyChallenge(userId: String, challengeId: Int, score: Int, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","challenge_id":$challengeId,"completed":true,"score":$score,"completed_at":${System.currentTimeMillis()}}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_daily_challenges")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Fetch flashcards grouped by lesson
    fun fetchFlashcards(callback: (List<FlashcardEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/flashcards?select=*&order=lesson_id.asc")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch flashcards: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, FlashcardEntity::class.java)
                        val adapter = moshi.adapter<List<FlashcardEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing flashcards: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching flashcards: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Fetch XP events for streak calendar
    fun fetchXPEvents(userId: String, callback: (List<XPEventEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/xp_events?user_id=eq.$userId&select=created_at&order=created_at.asc")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch XP events: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, XPEventEntity::class.java)
                        val adapter = moshi.adapter<List<XPEventEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing XP events: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching XP events: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // Update review schedule (SM-2 algorithm)
    fun updateReviewSchedule(userId: String, flashcardId: Int, easeFactor: Float, intervalDays: Int, repetitions: Int, nextReview: Long, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","flashcard_id":$flashcardId,"ease_factor":$easeFactor,"interval_days":$intervalDays,"repetitions":$repetitions,"next_review":$nextReview}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/review_schedule")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    val responseStr = response.body?.string() ?: ""
                    callback(Result.failure(Exception("Failed: ${response.code} - $responseStr")))
                }
                response.close()
            }
        })
    }

    // Fetch review schedule for a user
    fun fetchReviewSchedule(userId: String, callback: (List<ReviewScheduleEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/review_schedule?user_id=eq.$userId&select=*&order=next_review.asc")
            .get()

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch review schedule: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ReviewScheduleEntity::class.java)
                        val adapter = moshi.adapter<List<ReviewScheduleEntity>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing review schedule: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e(TAG, "Failed response fetching review schedule: ${response.code}")
                    callback(null)
                }
                response.close()
            }
        })
    }

    // === Content table fetchers ===

    fun fetchPhases(callback: (List<PhaseEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/phases?select=*&order=order_index.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch phases: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, PhaseEntity::class.java)
                        val adapter = moshi.adapter<List<PhaseEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing phases: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchUnits(phaseId: Int? = null, callback: (List<UnitEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val url = if (phaseId != null)
            "$SUPABASE_URL/rest/v1/units?select=*&phase_id=eq.$phaseId&order=order_index.asc"
        else
            "$SUPABASE_URL/rest/v1/units?select=*&order=order_index.asc"
        val requestBuilder = Request.Builder().url(url).get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch units: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UnitEntity::class.java)
                        val adapter = moshi.adapter<List<UnitEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing units: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchLessons(unitId: Int? = null, callback: (List<LessonEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val url = if (unitId != null)
            "$SUPABASE_URL/rest/v1/lessons?select=*&unit_id=eq.$unitId&order=order_index.asc"
        else
            "$SUPABASE_URL/rest/v1/lessons?select=*&order=order_index.asc"
        val requestBuilder = Request.Builder().url(url).get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch lessons: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, LessonEntity::class.java)
                        val adapter = moshi.adapter<List<LessonEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing lessons: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchExercisesForLesson(lessonId: Int, callback: (List<ExerciseEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/exercises?select=*&lesson_id=eq.$lessonId&order=order_in_lesson.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch exercises: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ExerciseEntity::class.java)
                        val adapter = moshi.adapter<List<ExerciseEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing exercises: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchMilestoneTest(phaseId: Int, callback: (MilestoneTestEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/milestone_tests?select=*&phase_id=eq.$phaseId&limit=1")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch milestone test: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, MilestoneTestEntity::class.java)
                        val adapter = moshi.adapter<List<MilestoneTestEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing milestone test: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    // === Per-user state fetchers ===

    fun fetchHeartsState(userId: String, callback: (HeartsStateEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/hearts_state?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch hearts: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, HeartsStateEntity::class.java)
                        val adapter = moshi.adapter<List<HeartsStateEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing hearts: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchGemWallet(userId: String, callback: (GemWalletEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/gem_wallet?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch gem wallet: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, GemWalletEntity::class.java)
                        val adapter = moshi.adapter<List<GemWalletEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing gem wallet: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchUserStreaks(userId: String, callback: (UserStreaksEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_streaks?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user streaks: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserStreaksEntity::class.java)
                        val adapter = moshi.adapter<List<UserStreaksEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user streaks: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchUserLevels(userId: String, callback: (UserLevelsEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_levels?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user levels: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserLevelsEntity::class.java)
                        val adapter = moshi.adapter<List<UserLevelsEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user levels: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchPhaseUnlocks(userId: String, callback: (List<PhaseUnlockEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/phase_unlocks?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch phase unlocks: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, PhaseUnlockEntity::class.java)
                        val adapter = moshi.adapter<List<PhaseUnlockEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing phase unlocks: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchStreakFreezeInventory(userId: String, callback: (StreakFreezeInventoryEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/streak_freeze_inventory?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch streak freeze: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, StreakFreezeInventoryEntity::class.java)
                        val adapter = moshi.adapter<List<StreakFreezeInventoryEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing streak freeze: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    // === Mutation methods ===

    fun insertGemTransaction(userId: String, amount: Int, reason: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","amount":$amount,"reason":"${jsonEscape(reason)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/gem_transactions")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertXpEvent(userId: String, xpAmount: Int, eventType: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","xp_amount":$xpAmount,"event_type":"${jsonEscape(eventType)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/xp_events")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertUserProgressRow(userId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_progress")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertHeartsState(userId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","hearts_count":5,"last_refill_at":"${jsonEscape(java.time.Instant.now().toString())}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/hearts_state")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertGemWallet(userId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","balance":0}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/gem_wallet")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertUserStreaks(userId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","current_streak":0,"longest_streak":0,"total_xp":0,"total_lessons":0,"total_flashcards":0}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_streaks")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertUserLevels(userId: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","current_level":1,"current_xp":0,"xp_to_next":100}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_levels")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun upsertPhaseUnlock(userId: String, phaseId: Int, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","phase_id":$phaseId,"unlocked_at":"${jsonEscape(java.time.Instant.now().toString())}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/phase_unlocks")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun insertMilestoneAttempt(userId: String, testId: Int, score: Int, passed: Boolean, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","test_id":$testId,"score":$score,"passed":$passed,"completed_at":"${jsonEscape(java.time.Instant.now().toString())}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/milestone_attempts")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun fetchUserSettings(userId: String, callback: (UserSettingsEntity?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_settings?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user settings: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserSettingsEntity::class.java)
                        val adapter = moshi.adapter<List<UserSettingsEntity>>(listType)
                        callback(adapter.fromJson(responseStr)?.firstOrNull())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user settings: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun updateUserSettings(userId: String, settings: UserSettingsEntity, callback: (Result<Unit>) -> Unit) {
        val jsonAdapter = moshi.adapter(UserSettingsEntity::class.java)
        val json = jsonAdapter.toJson(settings)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_settings?user_id=eq.$userId")
            .patch(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun fetchBookmarks(userId: String, callback: (List<BookmarkEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/bookmarks?user_id=eq.$userId&select=*&order=created_at.desc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch bookmarks: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, BookmarkEntity::class.java)
                        val adapter = moshi.adapter<List<BookmarkEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing bookmarks: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun addBookmark(userId: String, lessonId: Int, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","lesson_id":$lessonId}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/bookmarks")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun removeBookmark(userId: String, lessonId: Int, callback: (Result<Unit>) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/bookmarks?user_id=eq.$userId&lesson_id=eq.$lessonId")
            .delete()
            .addHeader("Content-Type", "application/json")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    fun fetchGemTransactions(userId: String, callback: (List<GemTransactionEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/gem_transactions?user_id=eq.$userId&select=*&order=created_at.desc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch gem transactions: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, GemTransactionEntity::class.java)
                        val adapter = moshi.adapter<List<GemTransactionEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing gem transactions: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchListeningExercises(lessonId: Int, callback: (List<ListeningExerciseEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/listening_exercises?lesson_id=eq.$lessonId&select=*&order=order_in_lesson.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch listening exercises: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ListeningExerciseEntity::class.java)
                        val adapter = moshi.adapter<List<ListeningExerciseEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing listening exercises: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchUserWeakPoints(userId: String, callback: (List<UserWeakPointEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_weak_points?user_id=eq.$userId&select=*&order=accuracy_pct.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch weak points: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserWeakPointEntity::class.java)
                        val adapter = moshi.adapter<List<UserWeakPointEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing weak points: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun upsertWeakPoint(userId: String, skillTag: String, errorCount: Int, accuracyPct: Float, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","skill_tag":"${jsonEscape(skillTag)}","error_count":$errorCount,"accuracy_pct":$accuracyPct,"needs_review":true}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_weak_points")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    // === Pronunciation targets ===
    fun fetchPronunciationTargets(lessonId: Int, callback: (List<PronunciationTargetEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/pronunciation_targets?lesson_id=eq.$lessonId&select=*&order=id.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch pronunciation targets: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, PronunciationTargetEntity::class.java)
                        val adapter = moshi.adapter<List<PronunciationTargetEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing pronunciation targets: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun insertPronunciationAttempt(userId: String, targetId: Int, audioUrl: String, score: Float, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","target_id":$targetId,"audio_url":"${jsonEscape(audioUrl)}","score":$score,"attempted_at":"${jsonEscape(java.time.Instant.now().toString())}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/pronunciation_attempts")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    // === AI Chat messages ===
    fun fetchAIMessages(userId: String, callback: (List<AIChatMessageEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/ai_messages?user_id=eq.$userId&select=*&order=created_at.asc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch AI messages: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, AIChatMessageEntity::class.java)
                        val adapter = moshi.adapter<List<AIChatMessageEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing AI messages: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun insertAIMessage(userId: String, role: String, content: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","role":"${jsonEscape(role)}","content":"${jsonEscape(content)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/ai_messages")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    // === Push tokens ===
    fun fetchPushTokens(userId: String, callback: (List<String>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/push_tokens?user_id=eq.$userId&select=token")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch push tokens: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, Map::class.java)
                        val adapter = moshi.adapter<List<Map<String, String>>>(listType)
                        val list = adapter.fromJson(responseStr)
                        callback(list?.mapNotNull { it["token"] })
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing push tokens: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun insertPushToken(userId: String, token: String, callback: (Result<Unit>) -> Unit) {
        val json = """{"user_id":"${jsonEscape(userId)}","token":"${jsonEscape(token)}"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/push_tokens")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { callback(Result.failure(e)) }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) callback(Result.success(Unit))
                else callback(Result.failure(Exception("Failed: ${response.code}")))
                response.close()
            }
        })
    }

    // === All daily challenges ===
    fun fetchAllDailyChallenges(callback: (List<DailyChallengeEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/daily_challenges?select=*&order=date.desc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch daily challenges: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, DailyChallengeEntity::class.java)
                        val adapter = moshi.adapter<List<DailyChallengeEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing daily challenges: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    fun fetchUserDailyChallenges(userId: String, callback: (List<UserDailyChallengeEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_daily_challenges?user_id=eq.$userId&select=*")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch user daily challenges: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, UserDailyChallengeEntity::class.java)
                        val adapter = moshi.adapter<List<UserDailyChallengeEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing user daily challenges: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }

    // === Milestone attempts for result history ===
    fun fetchMilestoneAttempts(userId: String, testId: Int, callback: (List<MilestoneAttemptEntity>?) -> Unit) {
        val headers = getAuthHeaders()
        val requestBuilder = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/milestone_attempts?user_id=eq.$userId&test_id=eq.$testId&select=*&order=completed_at.desc")
            .get()
        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        client.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch milestone attempts: ${e.message}")
                callback(null)
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    try {
                        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, MilestoneAttemptEntity::class.java)
                        val adapter = moshi.adapter<List<MilestoneAttemptEntity>>(listType)
                        callback(adapter.fromJson(responseStr))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed parsing milestone attempts: ${e.message}")
                        callback(null)
                    }
                } else { callback(null) }
                response.close()
            }
        })
    }
}

// Auth response model
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val token_type: String,
    val user: AuthUser?
)

data class AuthUser(
    val id: String,
    val email: String?
)
