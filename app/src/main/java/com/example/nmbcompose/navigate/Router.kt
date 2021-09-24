package com.example.nmbcompose

data class RouterData(val route: String, val params: Map<String, String>?)
const val LAUNCHER = "launcher"//中间页面
const val HOME = "home"//首页
const val THREAD_DETAIL = "thread_detail"//串内