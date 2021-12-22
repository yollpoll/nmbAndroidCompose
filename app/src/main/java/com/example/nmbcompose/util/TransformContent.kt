/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan. 
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna. 
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus. 
 * Vestibulum commodo. Ut rhoncus gravida arcu. 
 */

package com.example.nmbcompose.util

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import java.util.regex.Pattern

fun TextView.transHtmlContent(
    content: Spanned,
    id: String,
    onClickListener: ((String) -> Unit)? = null
) {
    val spannableString = SpannableString(content)
    if ("9999999".equals(id, ignoreCase = true)) {
        //广告
        val urlSpans = spannableString.getSpans(0, content.length, URLSpan::class.java)
        if (urlSpans.isNotEmpty()) {
            spannableString.setSpan(
                MyClickableSpan(urlSpans[0].url), spannableString.getSpanStart(
                    urlSpans[0]
                ),
                spannableString.getSpanEnd(urlSpans[0]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    } else {
        val pattern = Pattern.compile(">>.*")
        val matcher = pattern.matcher(content)
        while (matcher.find()) {
            val group = matcher.group()
            Log.d("spq", "group>>>>>>>>>>>>>>>$group")
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClickListener?.invoke(group)
                }
            }, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }


    this.text = spannableString
    this.setLinkTextColor(Color.parseColor("#7cb342"))
    //使用自定义的MovementMethod,解决子view不能把事件传递给父view的问题
    //使用自定义的MovementMethod,解决子view不能把事件传递给父view的问题
    this.movementMethod = ClickSpanMovementMethod.getInstance()
    //setMovementMethod以后，会自动把this的focusable 设置为true影响外部的点击事件，需要手动设置false
    //setMovementMethod以后，会自动把this的focusable 设置为true影响外部的点击事件，需要手动设置false
    this.isFocusable = false
    this.isClickable = false
    this.isLongClickable = false
}