package com.example.nmbcompose.navigate

import android.os.Bundle

data class MyNavDestination(
    val name: String? = null,
    val args: Bundle? = null,
    val navType: NavType = NavType.NORMAL
)

enum class NavType() {
    NORMAL, POPUP
}