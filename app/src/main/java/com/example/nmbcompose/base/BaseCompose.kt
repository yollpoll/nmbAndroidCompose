/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.base

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.nmbcompose.RouteDispatcher
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.ui.screen.TAG
import com.example.nmbcompose.viewmodel.BaseViewModel
import com.example.nmbcompose.viewmodel.LauncherViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T : BaseViewModel<*>> BaseScreen(
    viewModel: T,
    navTo: (RouterData) -> Unit,
    navController: NavController? = null,
    content: @Composable (T) -> Unit
) {
    RouterScreen(viewModel = viewModel, navTo = navTo, content = content)
}

@Composable
fun <T : BaseViewModel<*>> RouterScreen(
    viewModel: T,
    navTo: (RouterData) -> Unit,
    navController: NavController? = null,
    content: @Composable (T) -> Unit
) {
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
            navController?.navigate(route = route)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is BaseViewModel.OneShotEvent.NavigateTo -> {
                    dispatcher.invoke(it.param)
                }
                is BaseViewModel.OneShotEvent.BackTo -> {
                    dispatcher.invoke(it.param)
                }
            }
        }
    }
    content.invoke(viewModel)
}