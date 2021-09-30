package com.example.nmbcompose.di.module

import com.example.nmbcompose.di.CommonRetrofitFactory
import com.example.nmbcompose.di.LauncherRetrofitFactory
import com.example.nmbcompose.net.commonRetrofitFactory
import com.example.nmbcompose.net.launcherRetrofitFactory
import com.yollpoll.framework.net.http.RetrofitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class NetModuleDI {

    @ViewModelScoped
    @Provides
    @LauncherRetrofitFactory
    fun provideLauncherRetrofitFactory(): RetrofitFactory {
        return launcherRetrofitFactory
    }

    @ViewModelScoped
    @Provides
    @CommonRetrofitFactory
    fun provideCommonRetrofitFactory(): RetrofitFactory {
        return commonRetrofitFactory
    }

}