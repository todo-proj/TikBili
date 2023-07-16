package com.benyq.tikbili.ui.base.mvi.extension


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.ui.base.mvi.ContainerLazy
import com.benyq.tikbili.ui.base.mvi.MutableContainer
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState

/**
 * 构建viewModel的Ui容器，存储Ui状态和一次性事件
 */
fun <STATE : UiState, SINGLE_EVENT : UiEvent> ViewModel.containers(
    initialState: STATE,
): Lazy<MutableContainer<STATE, SINGLE_EVENT>> {
    return ContainerLazy(initialState, viewModelScope)
}