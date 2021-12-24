/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.viewmodel

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmbcompose.HOME
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.constant.EVENT_TITLE
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.repository.HomeRepository
import com.yollpoll.framework.extend.getList
import com.yollpoll.framework.message.liveeventbus.LiveEventBus
import com.yollpoll.framework.message.liveeventbus.ObserverWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) : BaseViewModel<MainScreenViewModel.MainAction>(context) {
    //标题变更bus
    private val onTitleChange = BusObserver<String> {
        _title.value = it
    }

    init {
        LiveEventBus.use(EVENT_TITLE, String::class.java).observeForever(onTitleChange)
    }

    override fun onCleared() {
        LiveEventBus.use(EVENT_TITLE, String::class.java).removeObserver(onTitleChange)
        super.onCleared()
    }

    //当前选择的板块
    private var _selectForum = MutableLiveData<ForumDetail>()
    val selectForum: LiveData<ForumDetail> = _selectForum

    //当前路由地址
    private var _currentDestination = MutableLiveData<String>(HOME)
    val currentDestination: LiveData<String> = _currentDestination


    //标题栏
    private var _title = MutableLiveData<String>("时间线")
    val title: LiveData<String> = _title


    //板块列表
    val listForum = flow {
        getList<Forum>(KEY_FORUM_LIST)?.let {
            _selectForum.value = it.first().forums.first()
            emit(it)
        }
    }


    override fun onAction(action: MainAction) {
        when (action) {
            is MainAction.OnForumSelect -> {
                _selectForum.value = action.forum
                _title.value = _selectForum.value?.name ?: run { "匿名版" }
            }
            is MainAction.OnNavTo -> {
                Log.d(TAG, "onAction: ${action.destination}")
                _currentDestination.value = action.destination
            }
            is MainAction.OnArticleSelect -> {
                _title.value = action.article.title
            }
        }
    }

    sealed class MainAction : BaseUiAction() {
        class OnForumSelect(val forum: ForumDetail) : MainAction()
        class OnNavTo(val destination: String) : MainAction()
        class OnArticleSelect(val article: ArticleItem) : MainAction()
    }

}

class BusObserver<T>(private val block: (T) -> Unit) : ObserverWrapper<T>() {
    override fun isSticky(): Boolean = true
    override fun mainThread(): Boolean = true
    override fun onChanged(value: T) {
        block.invoke(value)
    }
}
