/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.widget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.example.nmbcompose.bean.Reply
import com.example.nmbcompose.util.DateTools

@Composable
fun CommonDialog(onDismissRequest: () -> Unit,content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            content.invoke()
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