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
import com.yollpoll.framework.message.liveeventbus.LiveEventBus
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
        val title = viewModel.title.observeAsState()

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
                    text = title.value ?: run { "匿名版" },
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
                            dispatcher,
                        ) { it ->
                            viewModel.onAction(MainScreenViewModel.MainAction.OnArticleSelect(it))
                        }
                    }
                }
                composable(THREAD_DETAIL.withParam()) {
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
                composable(IMAGE.withParam()) {
                    createArgument(navBackStackEntry = it) { param ->
                        ImageScreen(url = param["url"])
                    }
                }
            }
        }
    }
