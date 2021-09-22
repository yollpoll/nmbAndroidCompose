package com.example.nmbcompose.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.nmbcompose.HOME
import com.example.nmbcompose.constant.TAG
import com.example.nmbcompose.navigate.NavType
import com.example.nmbcompose.ui.theme.primary
import com.example.nmbcompose.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.log

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel,
    navTo: (String) -> Unit
) {
//    val viewSate by viewModel.viewState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is BaseViewModel.OneShotEvent.NavigateTo -> {
                    navTo.invoke(HOME)
                }
            }
        }
    }
    LauncherScreenView("加载中") {
        viewModel.onAction(LauncherUiAction.LoadUrl)
    }
}

@Composable
fun LauncherScreenView(launcherText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClick) {
            Text(text = launcherText)
        }
    }
}

@Preview
@Composable
fun LauncherScreenPreview() {
    LauncherScreenView("测试文字") {}
}