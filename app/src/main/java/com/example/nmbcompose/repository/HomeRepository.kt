package com.example.nmbcompose.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.di.LauncherRetrofitFactory
import com.example.nmbcompose.net.HttpService
import com.example.nmbcompose.net.TIME_LINE_ID
import com.example.nmbcompose.paging.BasePagingSource
import com.yollpoll.framework.net.http.RetrofitFactory
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import javax.inject.Inject

const val START_INDEX = 1

class HomeRepository @Inject constructor(@HomeRepositoryAnnotation val retrofitFactory: RetrofitFactory) :
    IRepository {
    private val service = retrofitFactory.createService(HttpService::class.java)

    /**
     * 获取串列表
     */
    suspend fun getThreadList(id: String, page: Int) = service.getThreadList(id, page)

    /**
     * 时间线
     */
    suspend fun getTimeLine(page: Int) = service.getTimeLine(page = page)

    /**
     * 获取串列表的pagingSource
     */
    fun getThreadsPagingSource(id: String): BasePagingSource<ArticleItem> {
        return object : BasePagingSource<ArticleItem>() {
            override suspend fun load(pos: Int): List<ArticleItem> {
                Log.d(TAG, "load: ${id},${pos}")
                return getThreadList(id, pos)
            }
        }
    }

    /**
     * 获取时间线
     */
    fun getTimeLinePagingSource(): BasePagingSource<ArticleItem> {
        return object : BasePagingSource<ArticleItem>() {
            override suspend fun load(pos: Int): List<ArticleItem> {
                Log.d(TAG, "load: pos is $pos")
                return getTimeLine(pos)
            }
        }
    }

}
