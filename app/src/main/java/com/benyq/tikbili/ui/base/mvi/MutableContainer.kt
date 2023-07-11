package com.benyq.tikbili.ui.base.mvi

import com.benyq.tikbili.ui.base.mvi.Container

interface MutableContainer<STATE : UiState, SINGLE_EVENT : UiEvent> :
    Container<STATE, SINGLE_EVENT> {

    fun updateState(action: STATE.() -> STATE)

    fun sendEvent(event: SINGLE_EVENT)

}