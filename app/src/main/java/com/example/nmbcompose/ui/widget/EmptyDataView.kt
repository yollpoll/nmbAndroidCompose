package com.example.nmbcompose.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.nmbcompose.R

@Preview
@Composable
fun EmptyDataView() {
    MaterialTheme() {
        Surface(color = Color.Blue) {
            Column(verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_round),
                    contentDescription = "emptyData"
                )
                Text(text = "暂无数据", color = Color.DarkGray)
            }
        }

    }

}