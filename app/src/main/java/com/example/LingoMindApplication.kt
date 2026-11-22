package com.example

import android.app.Application
import android.util.Log
import com.example.data.Supabase
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class semaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("semaCrash", "Uncaught exception on ${thread.name}", throwable)
            Firebase.crashlytics.recordException(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
        Supabase.init(this)
    }
}
