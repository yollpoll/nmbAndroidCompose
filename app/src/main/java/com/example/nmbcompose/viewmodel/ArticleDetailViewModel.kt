package com.example.nmbcompose.viewmodel

import android.content.Context
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) : BaseViewModel<HomeAction>(context) {

    override fun onAction(action: HomeAction) {
    }
}

sealed class HomeAction : BaseUiAction() {
}