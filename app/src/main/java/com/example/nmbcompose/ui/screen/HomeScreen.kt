package com.example.nmbcompose.ui.screen

import android.graphics.drawable.LevelListDrawable
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.nmbcompose.bean.ArticleItem
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.viewmodel.*
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch


val DRAWER_LAYOUT_WIDTH = 700.dp

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    HomeScreenView(viewModel)
}

@Composable
fun HomeScreenView(viewModel: HomeViewModel) {
    var viewState = viewModel.viewState.collectAsState()

    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        drawerContent = {
            viewState.value.forumList?.apply {
                DrawerContent(this) {
                    viewModel.onAction(HomeViewModel.HomeAction.OnForumSelect(it))
                }
            }
        },
        scaffoldState = state,
        topBar = {
            TitleBar(text = "匿名板", showMenu = true) {
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
//        drawerShape = DrawerShape(),
    ) {
        HomeView(viewState.value.threadPager)
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
        return Outline.Rectangle(
            Rect(
                0f,
                0f,
                DRAWER_LAYOUT_WIDTH.value/* width */,
                size.height /* height */
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
        Divider()
        SettingContent()
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
    Image(
        painter = rememberCoilPainter(request = realCover),
        contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )

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
            ForumDetailCard(content) {
                onClick.invoke(content)
            }
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
fun ForumDetailCard(forumDetail: ForumDetail, onClick: () -> Unit) {
    Surface(modifier = Modifier
        .clickable { onClick }
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
@Composable
fun HomeView(pager: Pager<Int, ArticleItem>) {
    ThreadList(pager = pager)
}

/**
 * 串列表
 */
@Composable
fun ThreadList(pager: Pager<Int, ArticleItem>) {
    val threadItems = pager.flow.collectAsLazyPagingItems()
    LazyColumn {
        items(threadItems) { item ->
            if (item == null) {
                ThreadPlaceHolder()
            } else {
                ThreadItem(item) {
                    //点击事件
                }
            }
        }
    }
}

/**
 * item
 */
@Composable
fun ThreadItem(item: ArticleItem, onClick: () -> Unit) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val surfaceColor: Color by animateColorAsState(
//        if (isExpanded) MaterialTheme.colors.primary else
            MaterialTheme.colors.surface,
    )
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
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(10))
            .requiredHeightIn(
                min = if (isExpanded) Dp.Infinity else 100.dp,
                max = if (isExpanded) Dp.Infinity else 200.dp
            )
                .fillMaxWidth()
//            .requiredHeightIn(min = 100.dp, max = 400.dp)
                .padding(all = 10.dp)
                .clickable {
                    onClick.invoke()
                    isExpanded = !isExpanded
                }
        ) {
            Row {
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

                AndroidView(
                    factory = { context ->
                        val tvContent = TextView(context)
                        tvContent.ellipsize = TextUtils.TruncateAt.END
                        return@AndroidView tvContent
                    },
                    update = {
                        val tvContent = it
                        tvContent.maxLines=if(isExpanded) Int.MAX_VALUE else 5
                        it.text = HtmlCompat.fromHtml(
                            item.content.let {
//                                if (!item.img.isNullOrEmpty()) {
//                                return@let "<p style=\"width:400px;\">" +
//                                        "<img src=\"${imgThumbUrl}${item.img}${item.ext}\" align=\"left\" width=\"520\" hspace=\"5\" vspace=\"5\" />" +
//                                        "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" +
//                                        "</p>"+
//                                        "$it"
//                                    return@let "<img src=\"${imgThumbUrl}${item.img}${item.ext}\"/>$it"
//                                } else {
                                return@let it
//                                }
                            }, HtmlCompat.FROM_HTML_MODE_COMPACT, null
//                            { url ->
//                                val dl = LevelListDrawable()
//                                val empty =
//                                    tvContent.context.resources.getDrawable(com.example.nmbcompose.R.mipmap.ic_img_loading)
////                                dl.addLevel(0, 0, empty)
//                                dl.setBounds(0, 0, 200, 200)
//
//
//                                val loader = ImageLoader(tvContent.context)
//                                val req = ImageRequest.Builder(tvContent.context)
//                                    .data(url)
//                                    .target { drawable ->
//
//                                        Log.d(TAG, "ThreadItem: $url ${drawable.bounds.right}")
//                                        dl.addLevel(1, 1, drawable)
//                                    }
//                                    .build()
//                                loader.enqueue(req)
//                                return@fromHtml dl
//                            }
                        ) { opening, tag, output, xmlReader ->
                        }
                    }
                )
                }
            }

        }
    }

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
