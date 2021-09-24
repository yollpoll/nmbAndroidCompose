package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nmbcompose.HOME
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumList
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.FORUM_LIST
import com.example.nmbcompose.net.launcherRetrofitFactory
import com.example.nmbcompose.repository.LauncherRepository
import com.example.nmbcompose.util.launcherText
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yollpoll.framework.extend.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LauncherViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: LauncherRepository
) :
    BaseViewModel<LauncherUiAction>(context) {

    init {
        initData()
    }

    /**
     * 获取真实url
     */
    private fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.loadRealUrl()
                async { refreshCover() }
                getForumList()
                event.send(OneShotEvent.NavigateTo(HOME))
            } catch (e: Exception) {
                Log.d(TAG, "getRealUrl error: ${e.message}")
            }
        }
    }

    /**
     * 获取封面真实地址
     */
    private suspend fun refreshCover() {
        try {
            repository.refreshCover()
        } catch (e: Exception) {
            Log.d(TAG, "refreshCover: 封面获取错误")
        }
    }

    /**
     * 板块列表
     */
    private suspend fun getForumList() {
        try {
            val list = repository.getForumList()
            saveList(KEY_FORUM_LIST, list)
        } catch (e: Exception) {
            Log.d(TAG, "getForumList error: ${e.message}")
        }
    }

    override fun onAction(action: LauncherUiAction) {

    }

}

//data class LauncherState(val launcherContent: String) : BaseViewState()

sealed class LauncherUiAction : BaseUiAction() {
    object LoadUrl : LauncherUiAction()
}