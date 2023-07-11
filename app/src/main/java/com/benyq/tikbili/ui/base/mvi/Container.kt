package com.benyq.tikbili.ui.base.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 状态容器，分别存储UI状态和单次事件，如果不包含单次事件，则使用[Nothing]
 */
interface Container<STATE : UiState, SINGLE_EVENT : UiEvent> {

    //ui状态流
    val uiStateFlow: StateFlow<STATE>

    //单次事件流
    val singleEventFlow: Flow<SINGLE_EVENT>

}