package com.example.data

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private const val SUPABASE_URL = "https://fpazflweyimrulwwvsdw.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_1zTxwQY4Vx-jTRaFxKYJQg__Yf703nG"

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Sync User Progress with Supabase (Upsert)
    fun syncUserProgress(progress: UserProgressEntity) {
        val jsonAdapter = moshi.adapter(UserProgressEntity::class.java)
        val json = jsonAdapter.toJson(progress)

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_progress")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .build()

        client.newCall(request).enqueue(object : Callback {
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

    // Fetch User Progress from Supabase (by ID)
    fun fetchUserProgress(id: Int, callback: (UserProgressEntity?) -> Unit) {
        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/user_progress?id=eq.$id&select=*")
            .get()
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
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
        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/writing_submissions")
            .post(requestBody)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .build()

        client.newCall(request).enqueue(object : Callback {
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
}
