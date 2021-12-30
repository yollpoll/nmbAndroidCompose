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
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.nmbcompose.HOME
import com.example.nmbcompose.IMAGE
import com.example.nmbcompose.THREAD_DETAIL
import com.example.nmbcompose.bean.Article
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.constant.EVENT_TITLE
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.TIME_LINE_ID
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.net.realUrl
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.HomeRepository
import com.example.nmbcompose.repository.LauncherRepository
import com.example.nmbcompose.withParam
import com.yollpoll.framework.extend.getList
import com.yollpoll.framework.extend.toJsonBean
import com.yollpoll.framework.extend.toMapBean
import com.yollpoll.framework.message.liveeventbus.LiveEventBus
import com.yollpoll.framework.message.liveeventbus.ObserverWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository,
    val launcherRepository: LauncherRepository
) : BaseViewModel<MainScreenViewModel.MainAction>(context) {
    //数据列表
    private var _threadPager =
        MutableLiveData(getCommonPager {
//            if (null == _selectForum.value || _selectForum.value!!.id == TIME_LINE_ID)
            repository.getTimeLinePagingSource()
//            else repository.getThreadsPagingSource(_selectForum.value!!.id)
        })
    val threadPager = _threadPager
    val threadFLow = _threadPager.value!!.flow.cachedIn(viewModelScope)

    //刷新
    val _refreshList = MutableLiveData<Boolean>(false)
    val refreshList: LiveData<Boolean> = _refreshList

    //当前选择的板块
    private var _selectForum = MutableLiveData<ForumDetail>()
    val selectForum: LiveData<ForumDetail> = _selectForum

    //当前路由地址
    private var _currentDestination = MutableLiveData<String>(HOME)
    val currentDestination: LiveData<String> = _currentDestination


    //标题栏
    private var _title = MutableLiveData<String>("时间线")
    val title: LiveData<String> = _title

    //封面
    private var _cover = MutableLiveData("")
    val cover: LiveData<String> = _cover


    //板块列表
    val listForum = flow {
        getList<Forum>(KEY_FORUM_LIST)?.let {
            _selectForum.value = it.first().forums.first()
            emit(it)
        }
    }

    init {
        viewModelScope.launch {
            refreshCover()
            Log.d(TAG, "cover: ${realCover}")
            _cover.value = realCover
        }
    }

    /**
     * 获取封面真实地址
     */
    private suspend fun refreshCover() {
        try {
            launcherRepository.refreshCover()
        } catch (e: Exception) {
            Log.d(TAG, "refreshCover: 封面获取错误")
        }
    }


    override fun onAction(action: MainAction) {
        when (action) {
            is MainAction.OnForumSelect -> {
                _selectForum.value = action.forum
                _title.value = _selectForum.value?.name ?: run { "匿名版" }
                loadData(_selectForum.value?.id)
            }
            is MainAction.OnNavTo -> {
                Log.d(TAG, "onAction: ${action.destination}")
                _currentDestination.value = action.destination
                when (action.destination) {
                    IMAGE.withParam() -> {
                        _title.value = "大图"
                    }
                    HOME -> {
                        _title.value = selectForum.value?.name ?: "匿名版"
                    }
                    THREAD_DETAIL.withParam() -> {
                        val articleDetail = action.arg?.toMapBean<String, String>()
                        articleDetail?.get("title")?.also {
                            Log.d(TAG, "onAction: $it")
                            _title.value = it
                        }
                    }
                }
            }
            is MainAction.OnArticleSelect -> {
                _title.value = action.article.title
            }
            is MainAction.OnRefresh -> {
                _refreshList.value = true
            }
        }
    }

    private fun loadData(forumId: String?) {
        Log.d(TAG, "loadData: $forumId")
        if (null == forumId)
            return
        if (forumId == TIME_LINE_ID) {
//            viewModelScope.launch {
//                _threadFLow.emit(getCommonPager {
//                    repository.getTimeLinePagingSource()
//                })
//            }
            _threadPager.value = getCommonPager {
                repository.getTimeLinePagingSource()
            }
        } else {
//            viewModelScope.launch {
//                _threadFLow.emit(getCommonPager {
//                    repository.getThreadsPagingSource(forumId)
//                })
//            }
            _threadPager.value = getCommonPager {
                repository.getThreadsPagingSource(forumId)
            }
        }
    }

    sealed class MainAction : BaseUiAction() {
        class OnForumSelect(val forum: ForumDetail) : MainAction()
        class OnNavTo(val destination: String, val arg: String? = null) : MainAction()
        class OnArticleSelect(val article: ArticleItem) : MainAction()
        class OnRefresh() : MainAction()
    }

}

class BusObserver<T>(private val block: (T) -> Unit) : ObserverWrapper<T>() {
    override fun isSticky(): Boolean = true
    override fun mainThread(): Boolean = true
    override fun onChanged(value: T) {
        block.invoke(value)
    }
}
