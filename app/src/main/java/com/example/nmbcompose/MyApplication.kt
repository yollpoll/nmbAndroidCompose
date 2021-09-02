package com.example.nmbcompose

import android.app.Application
import com.yollpoll.fast.FastApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : FastApplication() {
//    companion object {
//        lateinit var INSTANCE: MyApplication
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        INSTANCE = this
//    }
}