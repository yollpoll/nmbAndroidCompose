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
import androidx.collection.arrayMapOf
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nmbcompose.*
import com.example.nmbcompose.R
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.bean.ForumDetail
import com.example.nmbcompose.net.realCover
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.viewmodel.HomeViewModel
import com.example.nmbcompose.viewmodel.MainScreenViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.yollpoll.framework.extend.toJson
import com.yollpoll.framework.extend.toJsonBean
import com.yollpoll.framework.message.liveeventbus.LiveEventBus
import com.yollpoll.framework.utils.ToastUtil
import kotlinx.coroutines.launch
import okhttp3.Route
import kotlin.math.log

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
        val coverUrl = viewModel.cover.observeAsState()
        val lazyItems = viewModel.threadFLow.collectAsLazyPagingItems()
        val pager = viewModel.threadPager.observeAsState()


        var refreshList by remember {
            return@remember mutableStateOf(false)
        }

        val state = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        //这个路由是属于本screen内部嵌套的路由
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
            Log.d(TAG, "MainScreen onNav: ${destination}")
            viewModel.onAction(
                MainScreenViewModel.MainAction.OnNavTo(
                    destination.route ?: "",
                    arguments?.get("param") as String?
                )
            )
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
                DrawerLeft(listForum.value, coverUrl.value, onCoverClick = {
                    scope.launch {
                        state.drawerState.close()
                    }
                    dispatcher.invoke(
                        RouterData(
                            IMAGE,
                            hashMapOf("url" to it.replace("/", "_"))
                        )
                    )
                }) {
                    viewModel.onAction(MainScreenViewModel.MainAction.OnForumSelect(it))
                    scope.launch {
                        state.drawerState.close()
                    }
                }
            },
            drawerGesturesEnabled = currentDestination.value == HOME,
            floatingActionButton = {
                if (currentDestination.value == HOME) {
                    Surface(
                        elevation = 3.dp,
                        shape = CircleShape,
                        modifier = Modifier
                            .clickable {
                                refreshList = true
                            },
                    ) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "action menu",
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(20.dp),
                        )
                    }
                }
            },
            isFloatingActionButtonDocked = true,
        ) {
            if (refreshList && lazyItems.loadState.refresh != LoadState.Loading) {
                Log.d(TAG, "MainScreen: refresh " + lazyItems.loadState.refresh)
                lazyItems.refresh()
                refreshList = false
            }
            NavHost(navController = navController, startDestination = HOME) {
                composable(HOME) {
                    createArgument(it) { args ->
                        HomeScreen(
                            pager.value?.flow?.collectAsLazyPagingItems()!!,
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
