package com.example.nmbcompose.net

import com.example.nmbcompose.bean.Article
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumList
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URL

interface HttpService {
    @GET(ROOT_URL)
    suspend fun getRealUrl(): List<String>

    @GET(FORUM_LIST)
    suspend fun getForumList(): ForumList

    @GET(COVER)
    suspend fun refreshCover(): ResponseBody

    @GET(GET_ARTICLE)
    suspend fun getThreadList(@Query("id") id: String, @Query("page") page: Int): Article//获取串列表

    @GET(TIME_LINE)
    suspend fun getTimeLine(@Query("page") page: Int): Article//时间线

    @GET(GET_CHILD_ARTICLE)
    suspend fun getArticleDetail(
        @Query("id") id: String,
        @Query("page") page: Int
    ): ArticleItem//获取串内容

}