package com.example.atommovielisting.utilities

import android.util.Log
import com.example.atommovielisting.BuildConfig

object LogUtils {
    val inTestMode = BuildConfig.DEBUG

    fun log(msg: String) {
        if (!inTestMode) return
        Log.d("log Dan", msg)
    }
}