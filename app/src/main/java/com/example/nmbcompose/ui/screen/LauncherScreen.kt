package com.example.nmbcompose.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nmbcompose.HOME
import com.example.nmbcompose.RouteDispatcher
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.base.BaseScreen
import com.example.nmbcompose.base.RouterScreen
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.navigate.NavType
import com.example.nmbcompose.ui.theme.nmbAccentColor
import com.example.nmbcompose.ui.theme.primary
import com.example.nmbcompose.ui.theme.textColor
import com.example.nmbcompose.ui.theme.white
import com.example.nmbcompose.util.launcherText
import com.example.nmbcompose.util.refreshLauncherText
import com.example.nmbcompose.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.log

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    navTo: RouteDispatcher
) = BaseScreen(viewModel = viewModel, navTo = navTo) {
    LauncherScreenView(launcherText) {
        viewModel.onAction(LauncherUiAction.LoadUrl)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LauncherScreenView(launcherText: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) MaterialTheme.colors.surface else primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(text = "加载中。。。", color = white, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = launcherText, color = white)
    }
}

@Preview
@Composable
fun LauncherScreenPreview() {
    LauncherScreenView("测试文字") {}
}