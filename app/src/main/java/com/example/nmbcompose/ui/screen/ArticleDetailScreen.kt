package com.example.nmbcompose.ui.screen

import android.graphics.Paint
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toolbar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.nmbcompose.R
import com.example.nmbcompose.RouteDispatcher
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.bean.Article
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.ui.theme.nmbAccentColor
import com.example.nmbcompose.ui.theme.nmbSecondBg
import com.example.nmbcompose.ui.widget.*
import com.example.nmbcompose.util.DateTools
import com.example.nmbcompose.viewmodel.*
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.Dispatchers

private const val TAG = "ArticleDetailScreen"

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ArticleDetailScreen(
    viewModel: ArticleDetailViewModel,
    navTo: RouteDispatcher,
    onBack: () -> Unit
) = BaseScreen(viewModel = viewModel, navTo = navTo) {
    val replies = viewModel.pagerFlow.collectAsLazyPagingItems()

    val emptyRefresh =
        (replies.loadState.refresh == LoadState.Loading) && (replies.itemCount == 0)

    val refreshLoading = replies.loadState.refresh == LoadState.Loading

    var detailState = viewModel.articleDetail.collectAsState(initial = null)

    var showDialog by remember {
        mutableStateOf(false)
    }
    //当前选中的
    var linkedReplay = viewModel.linkedArticle.observeAsState().value

    var onLinkBlock: (String) -> Unit = {
        Log.d(TAG, "ArticleDetailScreen: $it")
        showDialog = true
        //选择链接
        viewModel.onAction(ArticleDetailAction.OnArticleLinked(it))
    }

    ThreadContent(
        content = detailState.value,
        threadItems = replies,
        empty = emptyRefresh,
        loading = refreshLoading,
        onRefresh = { replies.refresh() },
        onItemClick = {},
        onLinkClick = onLinkBlock
    )

    if (showDialog) {
        CommonDialog(onDismissRequest = { showDialog = false }) {
            ArticleContentItem(
                userid = linkedReplay?.userid ?: "userId",
                id = linkedReplay?.id ?: "id",
                date = DateTools.replaceTime(linkedReplay?.now) ?: "未知时间",
                content = linkedReplay?.content ?: "",
                img = "${linkedReplay?.img}${linkedReplay?.ext}",
                isAdmin = linkedReplay?.admin != "0",
                onLinkClick = onLinkBlock
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ThreadContent(
    content: ArticleItem?,
    threadItems: LazyPagingItems<Reply>,
    empty: Boolean,
    loading: Boolean,
    onRefresh: () -> Unit,
    onItemClick: (Reply) -> Unit,
    onLinkClick: ((String) -> Unit)? = null
) {
    LoadingContent(
        empty = empty,
        emptyContent = { FullScreenLoading() },
        loading = loading,
        onRefresh = { onRefresh.invoke() }) {
        ItemList(content, threadItems, onItemClick, onLinkClick = onLinkClick)
    }
}


/**
 * 串列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ItemList(
    header: ArticleItem?,
    threadItems: LazyPagingItems<Reply>,
    onItemClick: (Reply) -> Unit,
    onLinkClick: ((String) -> Unit)? = null
) {
    LazyColumn {
        item {
            ArticleContentItem(
                userid = header?.userid ?: "",
                id = header?.id ?: "",
                date = DateTools.replaceTime(header?.now) ?: "未知时间",
                content = header?.content ?: "",
                img = "${header?.img}${header?.ext}",
                isAdmin = header?.admin != "0",
                onLinkClick = onLinkClick
            )
        }
        items(threadItems) { item ->
            if (item == null) {
                PlaceHolder()
            } else {
                ArticleContentItem(
                    userid = item.userid ?: "",
                    id = item.id ?: "",
                    date = DateTools.replaceTime(item?.now) ?: "未知时间",
                    content = item.content ?: "",
                    img = "${item.img}${item.ext}",
                    isAdmin = item.admin != "0",
                    onLinkClick = onLinkClick
                )
            }
        }
    }
}

/**
 * 解析html的textview
 */
@Composable
fun Content(content: String) {
    AndroidView(
        factory = { context ->
            val tvContent = TextView(context)
            tvContent.ellipsize = TextUtils.TruncateAt.END
            return@AndroidView tvContent
        },
        update = {
            val tvContent = it
            it.text = HtmlCompat.fromHtml(
                content.let {
                    return@let it
                }, HtmlCompat.FROM_HTML_MODE_COMPACT, null
            ) { opening, tag, output, xmlReader ->
            }
        }
    )
}

/**
 * 空白占位符
 */
@Composable
fun PlaceHolder() {
    Card {
        Surface {
            Column(Modifier.height(100.dp)) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_round),
                    contentDescription = "place holder"
                )
            }
        }
    }

}
