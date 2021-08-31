package com.example.nmbcompose.di

import androidx.lifecycle.SavedStateHandle
import com.example.nmbcompose.repository.HomeRepository
import com.example.nmbcompose.viewmodel.HomeViewModel
import com.yollpoll.framework.net.http.RetrofitFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@InstallIn(ViewModelComponent::class)
@Module
class HomeModel {
    @Provides
    @ViewModelScoped
    fun provideRepository(@CommonRetrofitFactory retrofitFactory: RetrofitFactory): HomeRepository =
        HomeRepository(retrofitFactory)
}