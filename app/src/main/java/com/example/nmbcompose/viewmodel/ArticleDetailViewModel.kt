package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.ArticleDetailRepository
import com.example.nmbcompose.repository.HomeRepository
import com.example.nmbcompose.util.launcherText
import com.yollpoll.framework.extend.toJsonBean
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: ArticleDetailRepository
) : BaseViewModel<ArticleDetailAction>(context) {
    //viewState
    val uiState by mutableStateOf(ArticleDetailViewState())

    //这里需要重写get，不能直接赋值
    //因为argument是初始化vm以后赋值的，所以id的置需要每次使用的时候都获取最新的
    val id
        get() = arguments["id"]

    val articleDetail by lazy {
        repository.getArticleDetail(id ?: "")
    }
    val pagerFlow = getCommonPager {
        return@getCommonPager repository.getReply(id ?: "")
    }.flow.map {
        it.filter { it ->
            it.id != "9999999"
        }
    }.cachedIn(viewModelScope)

    //标题
    val title by lazy {
        flow {
            emit(arguments["title"] ?: "无标题")
        }
    }


    fun load() {

    }

    override fun onAction(action: ArticleDetailAction) {
        when {
            action is ArticleDetailAction.OnArticleLinked -> {
                uiState.showDialog = true
            }
        }
    }
}

sealed class ArticleDetailAction : BaseUiAction() {
    class OnArticleLinked(id: String) : ArticleDetailAction()
}

/**
 * UI变化
 */
class ArticleDetailViewState : BaseViewState() {
    var showDialog: Boolean = false
}
