package com.benyq.tikbili.ui.splash

import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */

data class SplashState(val count: Int): UiState

sealed class SplashEvent : UiEvent {
    object ToLoginEvent : SplashEvent()
    object ToMainEvent : SplashEvent()
}
