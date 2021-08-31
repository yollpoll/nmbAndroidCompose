package com.example.nmbcompose.ui.screen

import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nmbcompose.bean.Forum
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.ui.theme.black
import com.example.nmbcompose.ui.theme.primary
import com.example.nmbcompose.ui.theme.textColor
import com.example.nmbcompose.ui.widget.TitleBar
import com.example.nmbcompose.viewmodel.HomeState
import com.example.nmbcompose.viewmodel.HomeViewModel
import com.example.nmbcompose.viewmodel.MainViewModel
import com.example.nmbcompose.viewmodel.ViewState
import kotlinx.coroutines.launch

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
        drawerContent = { DrawerContent(viewState.value.forumList) },
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
        drawerShape = DrawerShape(),
    ) {
        HomeView()
    }
}


/**
 * 主内容
 */
@Preview
@Composable
fun HomeView() {
    ForumList(list = arrayListOf("21312", "121312", "12131"))
}

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
                700f/* width */,
                size.height /* height */
            )
        )
    }
}

/**
 * 抽屉内容
 */
@Composable
fun DrawerContent(list: ArrayList<Forum>) {
    Log.d(TAG, "DrawerContent: ${list.size}")
    LazyColumn {
        items(list) { content ->
            Text(text = content.name, color = black)
        }
    }
}

/**
 *板块列表
 */
@Composable
fun ForumList(list: List<String>) {
    LazyColumn {
        items(list) { content ->
            Text(text = content, color = MaterialTheme.colors.primary)
        }
    }
}
