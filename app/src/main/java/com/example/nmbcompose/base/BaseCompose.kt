/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.nmbcompose.RouterData
import com.example.nmbcompose.viewmodel.BaseViewModel
import com.example.nmbcompose.viewmodel.LauncherViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T : BaseViewModel<*>> BaseScreen(
    viewModel: T,
    navTo: (RouterData) -> Unit,
    content: @Composable (T) -> Unit
) = RouterScreen(viewModel = viewModel, navTo = navTo, content = content)


@Composable
fun <T : BaseViewModel<*>> RouterScreen(
    viewModel: T,
    navTo: (RouterData) -> Unit,
    content: @Composable (T) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is BaseViewModel.OneShotEvent.NavigateTo -> {
                    navTo.invoke(it.param)
                }
                is BaseViewModel.OneShotEvent.BackTo -> {
                }
            }
        }
    }
    content.invoke(viewModel)
}