package com.example.nmbcompose.di

import com.example.nmbcompose.repository.HomeRepository
import com.example.nmbcompose.repository.IRepository
import com.example.nmbcompose.repository.LauncherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LauncherRepositoryDI {
    @Binds
    @LauncherRepositoryAnnotation
    abstract fun provideRepository(launcherRepository: LauncherRepository): IRepository

    @Binds
    @HomeRepositoryAnnotation
    abstract fun provideHomeRepository(launcherRepository: HomeRepository): IRepository
}