package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumList
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.repository.HomeRepository
import com.squareup.moshi.JsonClass
import com.yollpoll.framework.extend.getBean
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//关于hilt，如果在
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    val repository: HomeRepository
) :
    BaseViewModel<HomeAction, HomeState>(HomeState(), context) {


    override fun onAction(action: HomeAction) {
    }

    init {
        loadForumList()
    }

    fun loadForumList() {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.value.forumList = repository.getForumList()
        }
    }


}

@JsonClass(generateAdapter = true)
data class HomeState(var forumList: ArrayList<Forum> = arrayListOf()) : BaseViewState()

sealed class HomeAction : BaseUiAction()