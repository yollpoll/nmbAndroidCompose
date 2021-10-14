package com.example.nmbcompose

import android.util.Log
import androidx.navigation.NavController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

data class RouterData(val route: String, val params: Map<String, String>?)
abstract class RouteDispatcher : (RouterData) -> Unit

const val PARAM = "{param}"
const val LAUNCHER = "launcher"//中间页面
const val HOME = "home"//首页
const val MAIN = "MAIN"
const val THREAD_DETAIL = "thread_detail"//串内

fun getRouteWithParam(route: String): String {
    return "${route}/${PARAM}"
}