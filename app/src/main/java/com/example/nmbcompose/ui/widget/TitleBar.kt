package com.example.nmbcompose.ui.widget

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TitleBar(text: String, showMenu: Boolean, navigation: () -> Unit) {
    TopAppBar(
        title = { Text(text = "匿名板") },
        navigationIcon = {
            if (showMenu) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "menu",
                    modifier = Modifier.clickable {
                        navigation.invoke()
                    })
            } else {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "back",
                    modifier = Modifier.clickable {
                        navigation.invoke()
                    })
            }
        })
}