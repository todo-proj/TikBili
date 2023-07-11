package com.benyq.tikbili.ui.base.mvi

import com.benyq.tikbili.ui.base.mvi.internal.RealContainer
import kotlinx.coroutines.CoroutineScope

class ContainerLazy<STATE : UiState, SINGLE_EVENT : UiEvent>(
    initialState: STATE,
    parentScope: CoroutineScope
) : Lazy<MutableContainer<STATE, SINGLE_EVENT>> {

    private var cached: MutableContainer<STATE, SINGLE_EVENT>? = null

    override val value: MutableContainer<STATE, SINGLE_EVENT> =
        cached ?: RealContainer<STATE, SINGLE_EVENT>(initialState, parentScope).also { cached = it }

    override fun isInitialized() = cached != null
}