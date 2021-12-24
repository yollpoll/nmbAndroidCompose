/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.ui.screen.HtmlContentView
import com.google.accompanist.coil.rememberCoilPainter

/**
 * 串内容的item
 */
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ArticleContentItem(
    userid: String="userId",
    id: String="id",
    date: String="",
    content: String="内容",
    img: String?=null,
    isAdmin: Boolean = false,
    onLinkClick: ((String) -> Unit)? = null
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        elevation = 0.dp,
        // surfaceColor color will be changing gradually from primary to surface
        // animateContentSize will change the Surface size gradually
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                    },
                )
                .clip(RoundedCornerShape(10))
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                if (!img.isNullOrEmpty()) {
                    Image(
                        painter = rememberCoilPainter(
                            request = "$imgThumbUrl${img}",
                            fadeIn = true
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.Crop,
                    )
                }

                Column(
                    Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = userid, color =
                            if (!isAdmin) {
                                Color.Gray
                            } else {
                                Color.Red
                            }
                        )
                        Text(
                            text = id, color = MaterialTheme.colors.primaryVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    HtmlContentView(id = id, content = content, onLinkArticle = { id ->
                        onLinkClick?.invoke(id)
                    })
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
//        CommonDialog(showDialog) {
//            Text(text = "sasasasaa")
//        }
    }
    Divider(
        Modifier.padding(horizontal = 10.dp)
    )

}