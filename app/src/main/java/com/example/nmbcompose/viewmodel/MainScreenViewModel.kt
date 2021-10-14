/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nmbcompose.HOME
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.repository.HomeRepository
import com.yollpoll.framework.extend.getList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) : BaseViewModel<MainScreenViewModel.MainAction>(context) {
    //当前选择的板块
    private var _selectForum = MutableLiveData<ForumDetail>()
    val selectForum: LiveData<ForumDetail> = _selectForum

    //当前路由地址
    private var _currentDestination = MutableLiveData<String>(HOME)
    val currentDestination: LiveData<String> = _currentDestination


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
            }
            is MainAction.OnNavTo -> {
                Log.d(TAG, "onAction: ${action.destination}")
                _currentDestination.value = action.destination
            }
        }
    }

    sealed class MainAction : BaseUiAction() {
        class OnForumSelect(val forum: ForumDetail) : MainAction()
        class OnNavTo(val destination: String) : MainAction()
    }
}