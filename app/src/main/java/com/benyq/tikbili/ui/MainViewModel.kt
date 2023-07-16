package com.benyq.tikbili.ui

import androidx.lifecycle.SavedStateHandle
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
) : UiState

class ToastEvent : UiEvent

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val mainContainer by containers<MainState, UiEvent>(MainState("benyq", 26))

    var currentFragmentTag: String
        get() {
            return savedStateHandle["fragmentTag"] ?: ""
        }
        set(value) {
            savedStateHandle["fragmentTag"] = value
        }

}