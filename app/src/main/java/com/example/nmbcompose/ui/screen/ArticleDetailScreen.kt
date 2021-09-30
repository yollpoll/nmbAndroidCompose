package com.example.nmbcompose.ui.screen

import android.widget.Toolbar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.nmbcompose.RouteDispatcher
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.ui.widget.FullScreenLoading
import com.example.nmbcompose.ui.widget.LoadingContent
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.viewmodel.ArticleDetailViewModel
import com.example.nmbcompose.viewmodel.BaseUiAction
import com.example.nmbcompose.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun ArticleDetailScreen(
    viewModel: ArticleDetailViewModel,
    navTo: RouteDispatcher,
    articleId: String,
) = BaseScreen(viewModel = viewModel, navTo = navTo) {
    val title = viewModel.title.collectAsState(initial = "无标题")
    Content(title.value) {
    }
}

@Composable
fun Content(title: String, onBack: () -> Unit) {
    Scaffold(topBar = {
        TitleBar(text = title, false, onBack)
    }) {

    }
}

@Composable
fun ThreadContent(
    threadItems: LazyPagingItems<ArticleItem>,
    empty: Boolean,
    loading: Boolean,
    onRefresh: () -> Unit,
    onItemClick: (ArticleItem) -> Unit
) {
    LoadingContent(
        empty = empty,
        emptyContent = { FullScreenLoading() },
        loading = loading,
        onRefresh = { onRefresh.invoke() }) {
        ThreadList(threadItems, onItemClick)
    }
}

/**
 * 串列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ThreadList(threadItems: LazyPagingItems<Reply>, onItemClick: (ArticleItem) -> Unit) {
    LazyColumn {
        items(threadItems, key = {
            it.id
        }) { item ->
            if (item == null) {
                ThreadPlaceHolder()
            } else {
                ThreadItem(item, onItemClick)
            }
        }
    }
}