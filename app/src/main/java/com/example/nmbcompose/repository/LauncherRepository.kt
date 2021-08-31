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
    val service = retrofitFactory.createService(HttpService::class.java)

    @Throws(Exception::class)
    suspend fun loadRealUrl() {
        val url = service.getRealUrl()
        try {
            realUrl = url[0]
        } catch (e: Exception) {
            throw Exception("获取真实url失败")
        }
    }

    suspend fun getForumList() = service.getForumList()

}
