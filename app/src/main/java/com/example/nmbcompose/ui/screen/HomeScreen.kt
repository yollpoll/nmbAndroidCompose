package com.example.nmbcompose.ui.screen

import android.graphics.drawable.LevelListDrawable
import android.text.TextUtils
import android.util.Log
import android.widget.ImageButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
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
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.theme.*
import com.example.nmbcompose.ui.widget.FullScreenLoading
import com.example.nmbcompose.ui.widget.LoadingContent
import com.example.nmbcompose.ui.widget.TitleBar
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
fun HomeScreen(viewModel: HomeViewModel) {
    HomeScreenView(viewModel)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun HomeScreenView(viewModel: HomeViewModel) {
//    var viewState = viewModel.viewState.collectAsState()
    val listForum = viewModel.listForum.collectAsState(initial = arrayListOf())
    val threadPager = viewModel.threadPager.observeAsState()
    val selectForum = viewModel.selectForum.observeAsState()
    val threadItems = threadPager.value!!.flow.collectAsLazyPagingItems()

    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        drawerContent = {
            DrawerContent(listForum.value) {
                viewModel.onAction(HomeViewModel.HomeAction.OnForumSelect(it))
                scope.launch {
                    state.drawerState.close()
                }
            }
        },
        scaffoldState = state,
        topBar = {
            TitleBar(text = selectForum.value?.name ?: run { "匿名版" }, showMenu = true) {
                scope.launch {
                    if (state.drawerState.isOpen) {
                        state.drawerState.close()
                    } else {
                        state.drawerState.open()
                    }
                }
            }
        },
        drawerContentColor = contentColorFor(MaterialTheme.colors.background),
        drawerShape = DrawerShape(),
        floatingActionButton = {
            Surface(
                elevation = 3.dp,
                shape = CircleShape,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            scope.launch {
                                if (state.drawerState.isOpen) {
                                    state.drawerState.close()
                                } else {
                                    state.drawerState.open()
                                }
                            }
                        },
                        onLongClick = {},
                        onDoubleClick = { viewModel.refresh() },
                    ),
            ) {
                Icon(
                    Icons.Rounded.Edit,
                    contentDescription = "action menu",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.padding(20.dp),
                )
            }


        },
        isFloatingActionButtonDocked = true
    ) {
        val emptyRefresh =
            (threadItems.loadState.refresh == LoadState.Loading) && (threadItems.itemCount == 0)
        val refreshLoading = threadItems.loadState.refresh == LoadState.Loading
        HomeView(threadItems, emptyRefresh, refreshLoading, { threadItems.refresh() }) {
            viewModel.onAction(HomeViewModel.HomeAction.OnArticleClick(it))
        }
    }
}


//////////////////drawerLayout//////////////////

/**
 * 抽屉大小布局
 */
class DrawerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rounded(
            RoundRect(
                Rect(top = 0f, left = 0f, right = DRAWER_LAYOUT_WIDTH.value, bottom = size.height),
                bottomRight = CornerRadius(x = 30f, y = 30f),
                topRight = CornerRadius(x = 30f, y = 30f)
            )
        )
    }
}

/**
 * 抽屉内容
 */
@Composable
fun DrawerContent(list: List<Forum>, onClick: (ForumDetail) -> Unit) {
    Column {
        Cover()
//        Divider()
//        SettingContent()
        Divider()
        ForumList(list, onClick)
    }
}

/**
 * 设置
 */
@Composable
fun SettingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SettingItem(title = "搜索") {

        }
        SettingItem(title = "订阅") {

        }
        SettingItem(title = "设置") {

        }
        SettingItem(title = "作者") {

        }
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(all = 5.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(all = 10.dp),
        )
    }
}

/**
 * 封面
 */
@Composable
fun Cover() {
    if (realCover.isNullOrEmpty()) {
        Image(
            painter = painterResource(id = com.example.nmbcompose.R.mipmap.ic_img_loading),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = rememberCoilPainter(request = realCover),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }


}

/**
 *板块列表
 */
@Composable
fun ForumList(list: List<Forum>, onClick: (ForumDetail) -> Unit) {
    val detailList = list.flatMap {
        it.forums
    }
    LazyColumn {
        items(detailList) { content ->
            ForumDetailCard(content, onClick)
        }
    }
}

/**
 * 大板块item
 */
@Composable
fun forumCard(forum: Forum) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
//        Text(text = forum.name, color = MaterialTheme.colors.primary)
        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }
        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        )
        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = forum.name,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = forum.forums[0].msg!!,
                    modifier = Modifier
                        .padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

/**
 *板块列表
 */
@Composable
fun ForumDetailCard(forumDetail: ForumDetail, onClick: (ForumDetail) -> Unit) {
    Surface(modifier = Modifier
        .clickable { onClick.invoke(forumDetail) }
        .fillMaxWidth()
        .padding(all = 5.dp)
    ) {
        Text(
            text = forumDetail.name!!,
            modifier = Modifier
                .padding(all = 10.dp),
            style = MaterialTheme.typography.body2
        )
    }
}


//////////////////主内容//////////////////


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
fun ThreadList(threadItems: LazyPagingItems<ArticleItem>, onItemClick: (ArticleItem) -> Unit) {
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
    Box(contentAlignment = Alignment.CenterEnd) {
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
                },
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
fun ThreadItem(item: ArticleItem, onClick: (ArticleItem) -> Unit) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    //颜色变化
    val surfaceColor: Color by animateColorAsState(
        if (isExpanded) nmbSecondBg else
            nmbBg,
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
                                    .clip(RoundedCornerShape(10)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    Column(
                        Modifier
                            .padding(all = 10.dp)
                    ) {
                        //使用textview
                        Text(text = item.title)
                        Text(text = item.userid, color = item.let {
                            if (it.admin == "0") {
                                return@let Color.DarkGray
                            } else {
                                return@let Color.Red
                            }
                        })
                        HtmlContent(item.content, isExpanded)
                    }
                }
                if (!isExpanded) {
                    Divider()
                }
                if (isExpanded) {
                    Column {
                        item.replys.forEach {
                            Divider()
                            replyItem(it)
                        }
                    }
                }

            }
        }

    }

}

@Composable
fun replyItem(item: Reply) {
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
                        .clip(RoundedCornerShape(10)),
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
            HtmlContent(item.content)
        }
    }
}

/**
 * 解析html的textview
 */
@Composable
fun HtmlContent(content: String, isExpanded: Boolean = true) {
    AndroidView(
        factory = { context ->
            val tvContent = TextView(context)
            tvContent.ellipsize = TextUtils.TruncateAt.END
            return@AndroidView tvContent
        },
        update = {
            val tvContent = it
            tvContent.maxLines = if (isExpanded) Int.MAX_VALUE else 5
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
