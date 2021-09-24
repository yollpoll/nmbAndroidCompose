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
import com.example.nmbcompose.bean.*
import com.example.nmbcompose.constant.KEY_FORUM_LIST
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.di.HomeRepositoryAnnotation
import com.example.nmbcompose.net.TIME_LINE_ID
import com.example.nmbcompose.paging.BasePagingSource
import com.example.nmbcompose.paging.getCommonPager
import com.example.nmbcompose.repository.HomeRepository
import com.squareup.moshi.JsonClass
import com.yollpoll.framework.extend.getBean
import com.yollpoll.framework.extend.getList
import com.yollpoll.framework.extend.getString
import com.yollpoll.framework.extend.toListBean
import com.yollpoll.framework.message.liveeventbus.Observable
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

//关于hilt，如果在
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val repository: HomeRepository
) :
    BaseViewModel<HomeViewModel.HomeAction>(context) {


    private var _threadPager =
        MutableLiveData(getCommonPager { return@getCommonPager repository.getTimeLinePagingSource() })
    val threadPager: LiveData<Pager<Int, ArticleItem>> = _threadPager

    private var _selectForum = MutableLiveData<ForumDetail>()
    val selectForum: LiveData<ForumDetail> = _selectForum

    private var _empty = MutableLiveData<Boolean>(true)//当前页面是否是空的（展示一个加载条）
    val empty: LiveData<Boolean> = _empty

    private var _load = MutableLiveData<Boolean>(true)//当前页面数据是否正在加载
    val load: LiveData<Boolean> = _load


    val listForum = flow<List<Forum>> {
        getList<Forum>(KEY_FORUM_LIST)?.let {
            _selectForum.value = it.first().forums.first()
            emit(it)
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnForumSelect -> {
                _empty.value = true
                _selectForum.value = action.forum
                loadData(action.forum.id)
            }
            is HomeAction.OnArticleClick -> {
//                event.send(OneShotEvent.NavigateTo)
            }
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadData(_selectForum.value?.id)
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
        class OnForumSelect(val forum: ForumDetail) : HomeAction()
        class OnArticleClick(val item: ArticleItem) : HomeAction()
    }
}

