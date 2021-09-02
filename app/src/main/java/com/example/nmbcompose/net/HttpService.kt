package com.example.nmbcompose.net

import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumList
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import java.net.URL

interface HttpService {
    @GET(ROOT_URL)
    suspend fun getRealUrl(): List<String>

    @GET(FORUM_LIST)
    suspend fun getForumList(): ForumList

    @GET(COVER)
    suspend fun refreshCover(): ResponseBody

}