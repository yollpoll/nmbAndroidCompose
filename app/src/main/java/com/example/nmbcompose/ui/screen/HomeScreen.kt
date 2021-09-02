package com.example.nmbcompose.ui.screen

import android.util.Log
import android.widget.ImageButton
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.net.COVER
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.theme.black
import com.example.nmbcompose.ui.theme.primary
import com.example.nmbcompose.ui.theme.textColor
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
                    viewModel.onAction(HomeAction.OnForumSelect(it))
                }
            }
        },
        scaffoldState = state,
        topBar = {
            TitleBar(text = "匿名板块", showMenu = true) {
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
        HomeView()
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


/**
 * 主内容
 */
@Preview
@Composable
fun HomeView() {
}