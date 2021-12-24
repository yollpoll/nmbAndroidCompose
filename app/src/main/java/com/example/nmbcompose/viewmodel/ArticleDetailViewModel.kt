package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.THREAD_DETAIL
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.constant.EVENT_TITLE
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.ArticleDetailRepository
import com.example.nmbcompose.repository.HomeRepository
import com.example.nmbcompose.util.launcherText
import com.yollpoll.framework.extend.toJsonBean
import com.yollpoll.framework.message.MessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: ArticleDetailRepository
) : BaseViewModel<ArticleDetailAction>(context) {
    //这里需要重写get，不能直接赋值
    //因为argument是初始化vm以后赋值的，所以id的置需要每次使用的时候都获取最新的
    val id
        get() = arguments["id"]


    val articleDetail by lazy {
        repository.getArticleDetail(id ?: "").map {
            MessageManager.getInstance().sendMessage(EVENT_TITLE, it?.title ?: "无标题")
            return@map it
        }
    }

    //pagingFlow
    val pagerFlow = getCommonPager {
        return@getCommonPager repository.getReply(id ?: "")
    }.flow.map {
        it.filter { it ->
            cacheList.add(it)
            it.id != "9999999"
        }
    }.cachedIn(viewModelScope)


    //缓存数据，用于查找引用
    private val cacheList = arrayListOf<Reply>()

    //当前引用数据
    private val _linkedArticle = MutableLiveData<Reply>()
    val linkedArticle: LiveData<Reply> = _linkedArticle

    //标题
    val title by lazy {
        flow {
            emit(arguments["title"] ?: "无标题")
        }
    }

    override fun onAction(action: ArticleDetailAction) {
        when {
            action is ArticleDetailAction.OnArticleLinked -> {
                var find = false
                cacheList.forEach {
                    if (it.id == action.id) {
                        find = true
                        _linkedArticle.value = it
                    }
                }
                if (!find) {
                    viewModelScope.launch(Dispatchers.IO) {
                        event.send(
                            OneShotEvent.NavigateTo(
                                RouterData(
                                    THREAD_DETAIL,
                                    hashMapOf(
                                        "id" to action.id,
                                    )
                                )
                            )
                        )
                    }
                }
                find = true
            }
        }
    }
}

sealed class ArticleDetailAction : BaseUiAction() {
    class OnArticleLinked(var id: String) : ArticleDetailAction()
}

/**
 * UI变化
 */
sealed class ArticleDetailViewState : BaseViewState() {
    class ShowDialog(var show: Boolean = false, title: String? = null, message: String? = null) :
        ArticleDetailViewState()
}
