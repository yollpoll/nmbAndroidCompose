/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.repository

import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.net.HttpService
import com.example.nmbcompose.paging.BasePagingSource
import com.yollpoll.framework.net.http.RetrofitFactory
import javax.inject.Inject

class ArticleDetailRepository @Inject constructor(val retrofitFactory: RetrofitFactory) :
    IRepository {
    private val service = retrofitFactory.createService(HttpService::class.java)

    //获取串的具体内容
    fun getArticleDetail(id: String): BasePagingSource<ArticleItem> {
        return object : BasePagingSource<ArticleItem>() {
            override suspend fun load(pos: Int): List<ArticleItem> {
                return service.getArticleDetail(id, pos)
            }
        }
    }
}