package com.example.nmbcompose.net

import android.util.Log
import com.example.nmbcompose.constant.TAG
import okhttp3.Interceptor
import okhttp3.Response

class CoverImgInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val orgUrl = chain.request().url.toUrl().toString()
            val response = chain.proceed(chain.request())
            val realUrl = response.request.url.toUrl().toString()
            if (orgUrl == COVER) {
                //拦截到封面请求
                realCover = realUrl
            }
            return response
        } catch (e: Exception) {

        }
        return chain.proceed(chain.request())
    }
}