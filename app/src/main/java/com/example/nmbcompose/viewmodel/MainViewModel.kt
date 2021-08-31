package com.example.nmbcompose.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nmbcompose.HOME
import com.example.nmbcompose.LAUNCHER
import com.example.nmbcompose.navigate.MyNavDestination
import com.example.nmbcompose.navigate.NavType
import com.example.nmbcompose.util.launcherText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var currentRoute = MyNavDestination(LAUNCHER)

    //跳转
    private val _destination = MutableStateFlow(MyNavDestination())
    val destination = _destination.asStateFlow()

    //viewState
    private val _viewState = MutableStateFlow<ViewState>(ViewState())
    val viewState = _viewState.asStateFlow()

    //发送的事件
    private val _event = Channel<OneShotEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    //navigation 的状态时刻同步到 viewModel
    fun bindNavStack(navController: NavController) {
        val backStackEntry = navController.currentBackStackEntryFlow
        viewModelScope.launch {
            backStackEntry.collect {
                it.destination.route
            }
        }
        navController.addOnDestinationChangedListener { navControl, navDestination, arguments ->
            run {
                Log.i(
                    "spq",
                    "route: ${navDestination.route} destination: ${navDestination} argument:${arguments}"
                )
                currentRoute = MyNavDestination(navDestination.route)
//                _destination.value = MyNavDestination(requireNotNull(navDestination.route))
            }
        }
    }

    /**
     * 事件传递
     */
    fun onAction(action: UIAction) {
        when (action) {
            is UIAction.ActionNav -> {
                _destination.value =
                    MyNavDestination(action.destinationName, navType = action.navType)
            }
        }
    }
}

/**
 * vm发送给view的事件
 * 尽量用state代替事件
 */
sealed class OneShotEvent {
    object NavigateToResults : OneShotEvent()
}

/**
 * UI变化
 */
class ViewState {
    //加载文字
    var launchContent = launcherText
}

/**
 * UI事件类型
 */
sealed class UIAction {
    class ActionNav(val destinationName: String, val navType: NavType = NavType.NORMAL) : UIAction()
}
