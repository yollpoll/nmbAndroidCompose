package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nmbcompose.bean.*
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.HomeRepository
import com.squareup.moshi.JsonClass
import com.yollpoll.framework.extend.getBean
import com.yollpoll.framework.extend.getList
import com.yollpoll.framework.extend.getString
import com.yollpoll.framework.extend.toListBean
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//关于hilt，如果在
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) :
    BaseViewModel<HomeViewModel.HomeAction, HomeViewModel.HomeState>(
        HomeState(threadPager = getCommonPager { repository.getTimeLinePagingSource() }), context
    ) {

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnForumSelect -> {
                refreshThreadFlow(action.forum.id)
            }
        }
    }

    init {
        loadForumList()
    }

    fun loadForumList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                viewState.value.forumList = getList(KEY_FORUM_LIST)
            } catch (e: Exception) {
                Log.d(TAG, "loadForumList: ${e.message}")
            }
        }
    }

    fun refreshThreadFlow(forumId: String) {
        viewState.value.threadPager = getCommonPager {
            repository.getThreadsPagingSource(forumId)
        }
    }

    @JsonClass(generateAdapter = true)
    data class HomeState(
        var currentForumName: String = " ",
        var forumList: List<Forum>? = arrayListOf(),
        var threadPager: Pager<Int, ArticleItem>
    ) : BaseViewState()

    sealed class HomeAction : BaseUiAction() {
        class OnForumSelect(val forum: ForumDetail) : HomeAction()
    }
}

