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
import com.example.nmbcompose.net.launcherRetrofitFactory
import com.example.nmbcompose.repository.LauncherRepository
import com.example.nmbcompose.ui.screen.ForumList
import com.example.nmbcompose.util.launcherText
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yollpoll.framework.extend.saveBean
import com.yollpoll.framework.extend.saveStringToDataStore
import com.yollpoll.framework.extend.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class LauncherViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: LauncherRepository
) :
    BaseViewModel<LauncherUiAction, LauncherState>(LauncherState(launcherText), context) {

    init {
        getRealUrl()
    }

    /**
     * 获取真实url
     */
    private fun getRealUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadRealUrl()
            event.send(OneShotEvent.NavigateTo)
        }
    }

    /**
     * 板块列表
     */
    private suspend fun getForumList() {
        var json: String = ""
        try {
            var bean = repository.getForumList()

        } catch (e: Exception) {
            Log.d(TAG, "getForumList: ${e.message}")
        } finally {
            Log.d(TAG, "getForumList: finally $json")
        }
    }

    override fun onAction(action: LauncherUiAction) {

    }

}

data class LauncherState(val launcherContent: String) : BaseViewState()

sealed class LauncherUiAction : BaseUiAction() {
    object LoadUrl : LauncherUiAction()
}