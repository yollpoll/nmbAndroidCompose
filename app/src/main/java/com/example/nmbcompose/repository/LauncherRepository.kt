package com.example.nmbcompose.repository

import android.util.Log
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.LauncherRetrofitFactory
import com.example.nmbcompose.net.DIRECT_BASE_URL
import com.example.nmbcompose.net.HttpService
import com.example.nmbcompose.net.realUrl
import com.yollpoll.framework.net.http.RetrofitFactory
import kotlinx.coroutines.flow.collectLatest
import java.lang.Exception
import javax.inject.Inject


class LauncherRepository @Inject constructor(
    @LauncherRetrofitFactory val retrofitFactory: RetrofitFactory
) : IRepository {
    private val service by lazy {
        retrofitFactory.createService(HttpService::class.java)
    }

    @Throws(Exception::class)
    suspend fun loadRealUrl() {
        try {
            val url = service.getRealUrl()
            realUrl = url[0]
            Log.d(TAG, "loadRealUrl: $realUrl")
        } catch (e: Exception) {
            realUrl = DIRECT_BASE_URL
            throw Exception("获取真实url失败:${e.message}")
        }
    }

    suspend fun getForumList() = service.getForumList()

    //获取封面真实地址
    suspend fun refreshCover() = service.refreshCover()

}
