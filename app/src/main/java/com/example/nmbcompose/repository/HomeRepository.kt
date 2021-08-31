package com.example.nmbcompose.repository

import android.util.Log
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.di.LauncherRetrofitFactory
import com.example.nmbcompose.net.HttpService
import com.yollpoll.framework.net.http.RetrofitFactory
import javax.inject.Inject

class HomeRepository @Inject constructor(@HomeRepositoryAnnotation val retrofitFactory: RetrofitFactory) :
    IRepository {
    private val service = retrofitFactory.createService(HttpService::class.java)

    suspend fun getForumList() = service.getForumList()
}