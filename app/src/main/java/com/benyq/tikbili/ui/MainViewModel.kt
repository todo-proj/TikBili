package com.benyq.tikbili.ui

import androidx.lifecycle.ViewModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState
import com.benyq.tikbili.ui.base.mvi.extension.containers

/**
 *
 * @author benyq
 * @date 7/11/2023
 *
 */

data class MainState(
    val name: String,
    val age: Int
): UiState

class ToastEvent: UiEvent

class MainViewModel: ViewModel() {

    private val mainContainer by containers(MainState("benyq", 26))
}