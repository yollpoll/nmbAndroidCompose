/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.nmbcompose.*
import com.example.nmbcompose.R
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.viewmodel.MainScreenViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

/**
 * 带有标题栏的screen页面的结构页面
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MainScreen(viewModel: MainScreenViewModel, navTo: RouteDispatcher) =
    BaseScreen(viewModel, navTo) {
        //当前子页面地址

        val listForum = viewModel.listForum.collectAsState(initial = arrayListOf())
        val selectForum = viewModel.selectForum.observeAsState()
        val currentDestination = viewModel.currentDestination.observeAsState()


        val state = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        //路由
        val navController = rememberNavController()
        val dispatcher = object : RouteDispatcher() {
            override fun invoke(data: RouterData) {
                val url = data.route
                var param: String? = null
                data.params?.let {
                    val type = Types.newParameterizedType(
                        Map::class.java,
                        String::class.java,
                        String::class.java
                    )
                    param = Moshi.Builder().build().adapter<Map<String, String>>(type)
                        .toJson(data.params)
                }
                val route = "${url}${param?.run { "/${this}" } ?: ""}"
                Log.d(TAG, "invoke: $route")
                navController.navigate(route = route)
            }
        }
        //路由监听
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.onAction(MainScreenViewModel.MainAction.OnNavTo(destination.route ?: ""))
        }
        Scaffold(
            scaffoldState = state,
            topBar = {
                TitleBar(
                    text = selectForum.value?.name ?: run { "匿名版" },
                    showMenu = currentDestination.value == HOME
                ) {
                    if (currentDestination.value == HOME) {
                        //menu
                        scope.launch {
                            if (state.drawerState.isOpen) {
                                state.drawerState.close()
                            } else {
                                state.drawerState.open()
                            }
                        }
                    } else {
                        //back
                        navController.popBackStack()
                    }
                }
            },
            drawerContentColor = contentColorFor(MaterialTheme.colors.background),
            drawerShape = DrawerLeftShape(),
            drawerContent = {
                DrawerLeft(listForum.value) {
                    viewModel.onAction(MainScreenViewModel.MainAction.OnForumSelect(it))
                    scope.launch {
                        state.drawerState.close()
                    }
                }
            },
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
                            onDoubleClick = {},
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
            NavHost(navController = navController, startDestination = HOME) {
                composable(HOME) {
                    createArgument(it) {
                        HomeScreen(
                            createViewModel(),
                            dispatcher
                        )
                    }
                }
                composable(
                    getRouteWithParam(THREAD_DETAIL),
                ) {
                    createArgument(it) { args ->
                        ArticleDetailScreen(
                            createViewModel(
                                args = args
                            ),
                            dispatcher,
                        ) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }

/**
 * 抽屉大小布局
 */
class DrawerLeftShape : Shape {
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
fun DrawerLeft(list: List<Forum>, onClick: (ForumDetail) -> Unit) {
    Column {
        DrawerCover()
//        Divider()
//        SettingContent()
        Divider()
        DrawerForumList(list, onClick)
    }
}

@Composable
fun DrawerSettingItem(title: String, onClick: () -> Unit) {
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
fun DrawerCover() {
    if (realCover.isNullOrEmpty()) {
        Image(
            painter = painterResource(id = R.mipmap.ic_img_loading),
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
fun DrawerForumList(list: List<Forum>, onClick: (ForumDetail) -> Unit) {
    val detailList = list.flatMap {
        it.forums
    }
    LazyColumn {
        items(detailList) { content ->
            DrawerForumDetailCard(content, onClick)
        }
    }
}

/**
 * 大板块item
 */
@Composable
fun DrawerForumCard(forum: Forum) {
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
fun DrawerForumDetailCard(forumDetail: ForumDetail, onClick: (ForumDetail) -> Unit) {
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