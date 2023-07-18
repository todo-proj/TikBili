package com.benyq.tikbili.ui.main

import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */

data class MainState(
    val name: String,
    val age: Int,
) : UiState

sealed class MainPageEvent : UiEvent {

}