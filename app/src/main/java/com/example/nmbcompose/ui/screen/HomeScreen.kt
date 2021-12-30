package com.example.nmbcompose.ui.screen

import android.graphics.drawable.LevelListDrawable
import android.text.TextUtils
import android.util.Log
import android.widget.ImageButton
import android.widget.Space
import android.widget.TextView
import androidx.annotation.Dimension.DP
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.nmbcompose.RouteDispatcher
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.common.DrawerNestScrollConnection
import com.example.nmbcompose.ui.theme.*
import com.example.nmbcompose.ui.widget.FullScreenLoading
import com.example.nmbcompose.ui.widget.LoadingContent
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.util.TransFormContent
import com.example.nmbcompose.viewmodel.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


val DRAWER_LAYOUT_WIDTH = 700.dp

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    threadItems: LazyPagingItems<ArticleItem>,
    viewModel: HomeViewModel,
    navTo: RouteDispatcher,
    onItemClick: (ArticleItem) -> Unit
) =
    BaseScreen(viewModel = viewModel, navTo = navTo) {
        HomeScreenView(threadItems, viewModel, onItemClick)
    }

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreenView(
    threadItems: LazyPagingItems<ArticleItem>,
    viewModel: HomeViewModel,
    onItemClick: (ArticleItem) -> Unit
) {
//    val threadItems = viewModel.threadFLow.collectAsLazyPagingItems()
    Scaffold {
        val emptyRefresh =
            (threadItems.loadState.refresh == LoadState.Loading) && (threadItems.itemCount == 0)
        val refreshLoading = threadItems.loadState.refresh == LoadState.Loading
        HomeView(
            threadItems,
            emptyRefresh,
            refreshLoading,
            { threadItems.refresh() },
            onImageClick = {
                viewModel.onAction(HomeViewModel.HomeAction.OnImageClick(it))
            },
            onItemClick = {
                viewModel.onAction(HomeViewModel.HomeAction.OnArticleClick(it))
                onItemClick.invoke(it)
            })
    }

}


/**
 * 主内容
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeView(
    threadItems: LazyPagingItems<ArticleItem>,
    empty: Boolean,
    loading: Boolean,
    onRefresh: () -> Unit,
    onItemClick: ((ArticleItem) -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null
) {
    LoadingContent(
        empty = empty,
        emptyContent = { FullScreenLoading() },
        loading = loading,
        onRefresh = { onRefresh.invoke() }) {
        ThreadList(threadItems, onImageClick = onImageClick, onItemClick ?: {})
    }
}

/**
 * 串列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ThreadList(
    threadItems: LazyPagingItems<ArticleItem>,
    onImageClick: ((String) -> Unit)? = null,
    onItemClick: (ArticleItem) -> Unit
) {
    LazyColumn {
        items(threadItems) { item ->
            if (item == null) {
                ThreadPlaceHolder()
            } else {
                ThreadItem(item, onImageClick = onImageClick, onItemClick)
            }
        }
    }
}


/**
 * item状态，开启和关闭
 */
enum class ItemStatus {
    CLOSE, OPEN
}

/**
 * 滑动展开
 */
@ExperimentalMaterialApi
@Composable
fun SwipeItem(
    isExpended: Boolean = false,
    onCollect: () -> Unit = {},
    onReport: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val squareSize = 100.dp

    val swipeAbleState = rememberSwipeableState(initialValue = ItemStatus.CLOSE)


    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }

    val anchors = mapOf(0f to ItemStatus.CLOSE, sizePx to ItemStatus.OPEN)


    val reportColor: Color by animateColorAsState(
        if (isExpended) nmbAccentColor else
            nmbSecondBg,
    )

    val reportTextColor: Color by animateColorAsState(
        if (isExpended) white else
            nmbAccentColor,
    )

    Box(
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier = Modifier
                .swipeable(
                    state = swipeAbleState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(swipeAbleState.offset.value.roundToInt(), 0)
                }

        ) {
            content.invoke()
        }
        Column(modifier = Modifier.offset {
            IntOffset(-sizePx.roundToInt() + swipeAbleState.offset.value.roundToInt(), 0)
        }) {
            Button(
                onClick = { onCollect.invoke() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = reportColor,
                    contentColor = reportTextColor
                ),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 0.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
            ) {
                Text(
                    text = "举报",
                    modifier = Modifier
                        .alpha(
                            alpha = (swipeAbleState.offset.value / sizePx)
                        )
                        .width(squareSize),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onReport.invoke() }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = primary
                ),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 0.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )

            ) {
                Text(
                    text = "收藏",
                    modifier = Modifier
                        .alpha(
                            alpha = (swipeAbleState.offset.value / sizePx)
                        )
                        .width(squareSize),
                    textAlign = TextAlign.Center
                )
            }

        }


    }
}

/**
 * item
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ThreadItem(
    item: ArticleItem,
    onImageClick: ((String) -> Unit)? = null,
    onClick: (ArticleItem) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    //颜色变化
    val surfaceColor: Color by animateColorAsState(
        if (isSystemInDarkTheme()) MaterialTheme.colors.surface else {
            if (isExpanded) nmbSecondBg else nmbBg
        }
    )
    //高度变化
    val surfaceElevation: Dp by animateDpAsState(
        targetValue =
        if (isExpanded) 10.dp else
            0.dp,
    )
    //间隔变化
    val surfacePadding: Dp by animateDpAsState(
        targetValue =
        if (isExpanded) 20.dp else
            0.dp,
    )

    Surface(
        shape = MaterialTheme.shapes.medium,
        elevation = surfaceElevation,
        // surfaceColor color will be changing gradually from primary to surface
        color = surfaceColor,
        // animateContentSize will change the Surface size gradually
        modifier = Modifier
            .animateContentSize()
            .padding(surfacePadding)
    ) {
        SwipeItem(isExpanded) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            onClick.invoke(item)
                        },
                        onLongClick = {
                            isExpanded = !isExpanded
                        },
                    )
                    .clip(RoundedCornerShape(10))
                    .requiredHeightIn(
                        min = if (isExpanded) Dp.Infinity else 100.dp,
                        max = if (isExpanded) Dp.Infinity else 200.dp
                    )
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .requiredHeightIn(
                            min = if (isExpanded) Dp.Infinity else 100.dp,
                            max = if (isExpanded) Dp.Infinity else 200.dp
                        ), verticalAlignment = Alignment.Top
                ) {
                    item.apply {
                        if (!img.isNullOrEmpty()) {
                            Image(
                                painter = rememberCoilPainter(
                                    request = "${imgThumbUrl}${item.img}${item.ext}",
                                    fadeIn = true
                                ),
                                contentDescription = "",
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(10))
                                    .clickable {
                                        onImageClick?.invoke("${item.img}${item.ext}")
                                    },
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                    ) {
                        Row {
                            //使用textview
                            Text(text = item.title, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = item.userid, fontSize = 10.sp, color = item.let {
                                if (it.admin == "0") {
                                    return@let Color.DarkGray
                                } else {
                                    return@let Color.Red
                                }
                            })
                        }

                        HtmlContent(item.id, item.content, isExpanded)
                    }
                }
                if (!isExpanded) {
                    Divider()
                }
                if (isExpanded) {
                    Column {
                        item.replys.forEach {
                            Divider()
                            replyItem(it, onImageClick = onImageClick)
                        }
                    }
                }

            }
        }

    }

}

@Composable
fun replyItem(item: Reply, onImageClick: ((String) -> Unit)? = null) {
    Row(modifier = Modifier.padding(10.dp)) {
        item.apply {
            if (!img.isNullOrEmpty()) {
                Image(
                    painter = rememberCoilPainter(
                        request = "${imgThumbUrl}${item.img}${item.ext}",
                        fadeIn = true
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            onImageClick?.invoke("${item.img}${item.ext}")
                        },
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            Modifier
                .padding(all = 5.dp)
        ) {
            Row {
//                Text(text = item.title, fontSize = 12.sp)
                Text(text = "${item.userid}:", fontSize = 12.sp, color = item.let {
                    if (it.admin == "0") {
                        return@let Color.Gray
                    } else {
                        return@let Color.Red
                    }
                })
            }
            HtmlContent(item.id ?: "", item.content ?: "")
        }
    }
}

/**
 * 解析html的textview
 */
@Composable
fun HtmlContent(id: String, content: String, isExpanded: Boolean = true) {
    AndroidView(
        factory = { context ->
            val tvContent = TextView(context)
            tvContent.ellipsize = TextUtils.TruncateAt.END
            return@AndroidView tvContent
        },
        update = {
            val tvContent = it
            tvContent.maxLines = if (isExpanded) Int.MAX_VALUE else 5
            TransFormContent.trans(
                id, HtmlCompat.fromHtml(
                    content.let {
                        return@let it
                    }, HtmlCompat.FROM_HTML_MODE_COMPACT, null
                ) { opening, tag, output, xmlReader ->
                }, tvContent
            ) {
                Log.d(TAG, "HtmlContent: $it")
            }
        }
    )
}

/**
 * 空白占位符
 */
@Composable
fun ThreadPlaceHolder() {
    Card {
        Surface {
            Column(Modifier.height(100.dp)) {
                Image(
                    painter = painterResource(com.example.nmbcompose.R.mipmap.ic_launcher_round),
                    contentDescription = "place holder"
                )
            }
        }
    }

}
