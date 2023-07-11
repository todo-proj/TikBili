package com.benyq.tikbili.ui.base.mvi.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState
import com.benyq.tikbili.ui.base.mvi.internal.StateTuple2
import com.benyq.tikbili.ui.base.mvi.internal.StateTuple3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

fun <T : UiState> Flow<T>.collectState(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: StateCollector<T>.() -> Unit
) {
    StateCollector(this@collectState, lifecycleOwner, state).action()
}

fun <T : UiEvent> Flow<T>.collectSingleEvent(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectSingleEvent.collect {
                action(it)
            }
        }
    }
}

class StateCollector<T : UiState>(
    private val flow: Flow<T>,
    private val lifecycleOwner: LifecycleOwner,
    private val state: Lifecycle.State,
) {

    fun <A> collectPartial(
        prop1: KProperty1<T, A>,
        action: (A) -> Unit
    ) {
        flow.collectPartial(lifecycleOwner, prop1, state, action)
    }

    fun <A, B> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        action: (A, B) -> Unit
    ) {
        flow.collectPartial(lifecycleOwner, prop1, prop2, state, action)
    }

    fun <A, B, C> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        action: (A, B, C) -> Unit
    ) {
        flow.collectPartial(lifecycleOwner, prop1, prop2, prop3, state, action)
    }

}

internal fun <T : UiState, A> Flow<T>.collectPartial(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    state: Lifecycle.State,
    action: (A) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectPartial
                .map { prop1.get(it) }
                .distinctUntilChanged()
                .collect { partialState ->
                    action(partialState)
                }
        }
    }
}

internal fun <T : UiState, A, B> Flow<T>.collectPartial(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: (A, B) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectPartial
                .map { StateTuple2(prop1.get(it), prop2.get(it)) }
                .distinctUntilChanged()
                .collect { (partialStateA, partialStateB) ->
                    action(partialStateA, partialStateB)
                }
        }
    }
}

internal fun <T : UiState, A, B, C> Flow<T>.collectPartial(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    prop3: KProperty1<T, C>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: (A, B, C) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectPartial
                .map { StateTuple3(prop1.get(it), prop2.get(it), prop3.get(it)) }
                .distinctUntilChanged()
                .collect { (partialStateA, partialStateB, partialStateC) ->
                    action(partialStateA, partialStateB, partialStateC)
                }
        }
    }
}

