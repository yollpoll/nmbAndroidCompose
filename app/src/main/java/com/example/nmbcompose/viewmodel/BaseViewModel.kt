package com.example.nmbcompose.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.yollpoll.fast.FastViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.reflect.KClass

/**
 * T:UiAction
 * K:ViewState
 * @param state 初识状态下的ui数据
 */
abstract class BaseViewModel<T : BaseUiAction>( context: Context) :
    FastViewModel(context as Application) {
//    protected val _viewState = MutableStateFlow<K>(state)
//    val viewState = _viewState.asStateFlow()

    //发送的事件
    protected val event = Channel<OneShotEvent>(Channel.BUFFERED)
    val eventFlow = event.receiveAsFlow()

    abstract fun onAction(action: T)

    sealed class OneShotEvent() {
        //导航
        object NavigateTo : OneShotEvent()
    }

}

abstract class BaseViewState()
abstract class BaseUiAction()