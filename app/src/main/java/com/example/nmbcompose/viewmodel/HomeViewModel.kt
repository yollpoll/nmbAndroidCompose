package com.example.nmbcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nmbcompose.IMAGE
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.THREAD_DETAIL
import com.example.nmbcompose.bean.*
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.net.TIME_LINE_ID
import com.example.nmbcompose.net.imgUrl
import com.example.nmbcompose.paging.BasePagingSource
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.HomeRepository
import com.squareup.moshi.JsonClass
import com.yollpoll.framework.extend.*
import com.yollpoll.framework.message.liveeventbus.Observable
import com.yollpoll.framework.utils.ToastUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

//关于hilt，如果在
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) :
    BaseViewModel<HomeViewModel.HomeAction>(context) {

    private var _threadPager =
        MutableLiveData(getCommonPager {
            if (null == arguments["forumId"] || arguments["forumId"] == TIME_LINE_ID)
                repository.getTimeLinePagingSource()
            else repository.getThreadsPagingSource(arguments["forumId"]!!)
        })
    val threadFLow = _threadPager.value!!.flow.cachedIn(viewModelScope)

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnArticleClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    event.send(
                        OneShotEvent.NavigateTo(
                            RouterData(
                                THREAD_DETAIL,
                                hashMapOf(
                                    "id" to action.item.id,
                                    "title" to action.item.title,
                                )
                            )
                        )
                    )
                }
            }
            is HomeAction.OnImageClick -> {
                //查看大图
                viewModelScope.launch {
                    event.send(
                        OneShotEvent.NavigateTo(
                            RouterData(
                                IMAGE,
                                hashMapOf("url" to (imgUrl + action.url).replace("/", "_"))
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * 加载数据
     */
    private fun loadData(forumId: String?) {
        if (null == forumId)
            return
        if (forumId == TIME_LINE_ID) {
            _threadPager.value = getCommonPager {
                repository.getTimeLinePagingSource()
            }
        } else {
            _threadPager.value = getCommonPager {
                repository.getThreadsPagingSource(forumId)
            }
        }
    }


    sealed class HomeAction : BaseUiAction() {
        class OnArticleClick(val item: ArticleItem) : HomeAction()
        class OnImageClick(val url: String) : HomeAction()
    }
}

