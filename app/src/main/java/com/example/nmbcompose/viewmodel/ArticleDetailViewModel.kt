package com.example.nmbcompose.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.ArticleDetailRepository
import com.example.nmbcompose.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: ArticleDetailRepository
) : BaseViewModel<ArticleDetailAction>(context) {
    val id = arguments["id"]

    private var _threadPager =
        MutableLiveData(getCommonPager { return@getCommonPager repository.getArticleDetail(id!!) })
    val threadPager: LiveData<Pager<Int, ArticleItem>> = _threadPager

    //标题
    val title = flow {
        emit(arguments["title"] ?: "无标题")
    }

    init {

    }

    fun load() {

    }

    override fun onAction(action: ArticleDetailAction) {
    }
}

sealed class ArticleDetailAction : BaseUiAction() {
}