/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.widget

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommonDialog(
    show: MutableState<Boolean> = mutableStateOf(true),
    build: @Composable () -> Unit
) {
    if (show.value) {
        Dialog(onDismissRequest = { show.value = false }) {
            build.invoke()
        }
    }
}

@Composable
fun CommonAlertDialog(
    title: String,
    content: String,
    show: MutableState<Boolean> = mutableStateOf(true),
    build: @Composable () -> Unit
) {
    AlertDialog(
        backgroundColor = Color.White,
        onDismissRequest = { show.value = false },
        properties = DialogProperties(
        ),
        confirmButton = {
            TextButton(
                onClick = { show.value = false }) {
                Text(text = "确定")
            }
        })


}