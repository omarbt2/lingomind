package com.example.data

import android.content.Context
import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime

object Supabase {
    private var initialized = false

    private lateinit var client: SupabaseClient

    fun init(context: Context) {
        if (!initialized) {
            client = createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
            ) {
                install(Postgrest)
                install(Auth)
                install(Storage)
                install(Realtime)
            }
            initialized = true
        }
    }

    fun getClient(): SupabaseClient {
        if (!initialized) {
            throw IllegalStateException("Supabase not initialized. Call Supabase.init(context) first.")
        }
        return client
    }

    val postgrest: Postgrest
        get() = getClient().postgrest

    val auth: Auth
        get() = getClient().auth

    val storage: Storage
        get() = getClient().storage

    val realtime: Realtime
        get() = getClient().realtime
}
