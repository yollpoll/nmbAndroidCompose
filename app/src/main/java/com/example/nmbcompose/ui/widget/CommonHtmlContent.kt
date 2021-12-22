/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.screen

import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.example.nmbcompose.util.TransFormContent

@Composable
fun HtmlContentView(
    id: String,
    content: String,
    isExpanded: Boolean = true,
    onLinkArticle: ((String) -> Unit)? = null
) {
    AndroidView(
        factory = { context ->
            val tvContent = TextView(context)
            tvContent.ellipsize = TextUtils.TruncateAt.END
            return@AndroidView tvContent
        },
        update = {
            val tvContent = it
            tvContent.maxLines = if (isExpanded) Int.MAX_VALUE else 5
            TransFormContent.trans(
                id, HtmlCompat.fromHtml(
                    content.let {
                        return@let it
                    }, HtmlCompat.FROM_HTML_MODE_COMPACT, null
                ) { opening, tag, output, xmlReader ->
                }, tvContent
            ) {
                onLinkArticle?.invoke(it)
            }
        }
    )
}