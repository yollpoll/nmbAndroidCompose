package com.example.nmbcompose

import android.util.Log
import androidx.navigation.NavController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

//路由数据
data class RouterData(
    val route: String,
    val params: Map<String, String>?,
    var popBackStack: Boolean = false
)

//路由分发
abstract class RouteDispatcher : (RouterData) -> Unit

const val PARAM = "{param}"
const val LAUNCHER = "launcher"//中间页面
const val HOME = "home"//首页
const val MAIN = "MAIN"
const val THREAD_DETAIL = "thread_detail"//串内
const val IMAGE = "image"//大图

//扩展函数
fun String.withParam(): String {
    return "${this}/${PARAM}"
}

