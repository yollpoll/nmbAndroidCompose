/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.nmbcompose.ui.screen

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.nmbcompose.net.imgThumbUrl
import com.example.nmbcompose.net.imgUrl
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.imageloading.isFinalState
import com.yollpoll.framework.utils.ToastUtil

private const val TAG = "ImageScreen"

@Composable
fun ImageScreen(url: String?) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
//        rotation += rotationChange
        offset += offsetChange
//        Log.d(TAG, "ImageScreen: scale $scale rotation:$rotation offset:$offset")
    }
    var scaleType by remember {
        mutableStateOf(1)
    }
    val scaleAnimate by animateFloatAsState(targetValue = scale * scaleType)

    val rotationAnimate by animateFloatAsState(targetValue = rotation)

    Log.d(TAG, "ImageScreen: "+url?.replace("_", "/"))
    url?.let {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .graphicsLayer(
                    scaleX = scaleAnimate,
                    scaleY = scaleAnimate,
                    rotationZ = rotationAnimate,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state)
                //自定义的手势处理
                .pointerInput(Unit) {
                    //forEachGesture是讲一组手势放在一起回传给作用域
                    //如果不使用这个，只会调用一次回调
                    //内部使用while循环捕获事件
//                    forEachGesture {
//                        //捕获手势的作用域
//                        //协程作用域，在这里一直等待相应手势被捕获
//                        awaitPointerEventScope {
//                            //使用while一直捕获，否则只会不会按下的瞬间的事件
//                            var down1: PointerInputChange? = null
//                            var down2: PointerInputChange? = null
//                            while (true) {
//                                //自定义实现doubletap
////                                var event = waitForUpOrCancellation()
////                                if (null == down1) down1 = event else down2 = event
////                                if (null != down1 && null != down2) {
////                                    Log.d(
////                                        TAG,
////                                        "ImageScreen: ${down2.uptimeMillis}   ${down1.uptimeMillis}"
////                                    )
////                                    if (down2.uptimeMillis - down1.uptimeMillis < 200) {
////                                        scaleType = (scaleType) % 3 + 1
////                                    }
////                                    down1 = null
////                                    down2 = null
////                                }
//
//
//                                //自定义实现旋转到特定角度自动定位到0度-90度-180度等
////                                var event2 = awaitPointerEvent()
////                                //手势相关数据
////                                //判断事件类型以及相关操作
////                                if (event2.changes.any { it.changedToUp() }) {
////                                    //完成操作了，退出
////                                    rotation = lockRotation(rotation)
////                                    Log.d(TAG, "ImageScreen: size>>>>>${event2.changes.size}")
////                                    return@awaitPointerEventScope
////                                }
//                            }
//                        }
//                    }


                    detectTapGestures(onDoubleTap = {
                        Log.d(TAG, "ImageScreen: ondoubleclick")
                        scaleType = (scaleType) % 3 + 1
                    })
                }
                .fillMaxSize()
        ) {
            val painter =
                //没有带头地址
                rememberCoilPainter(request = url.replace("_", "/"), fadeIn = true)

            if (!painter.loadState.isFinalState()) {
                CircularProgressIndicator()
            }
            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
        }
    }
}

fun lockRotation(rotation: Float): Float {
    var ro = rotation
    var res = ro
    ro = ro % 360
    when {
        ro <= 45 && ro > -45 -> {
            res = 0f
        }
        ro > 45 && ro <= 135 -> {
            res = 90f

        }
        ro > 135 && ro <= 225 -> {
            res = 180f
        }
        ro > 225 && ro <= 315 -> {
            res = 270f
        }
        ro > 315 -> {
            res = 360f
        }

        ro < -45 && ro > -135 -> {
            res = -90f
        }
        ro <= -135 && ro > -225 -> {
            res = -180f
        }
        ro <= -225 && ro > -315 -> {
            res = -270f
        }
        ro <= -315 -> {
            res = -360f
        }
    }
    return res
}


