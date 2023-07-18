package com.benyq.tikbili.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.api.ApiThrowable
import com.benyq.tikbili.bilibili.BiliBiliConstant
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.UiEvent
import com.benyq.tikbili.ui.base.mvi.UiState
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *
 * @author benyq
 * @date 7/11/2023
 *
 */



class MainViewModel(private val savedStateHandle: SavedStateHandle) : BaseViewModel() {

    val mainContainer by containers<MainState, MainPageEvent>(MainState("benyq", 26))

    var currentFragmentTag: String
        get() {
            return savedStateHandle["fragmentTag"] ?: ""
        }
        set(value) {
            savedStateHandle["fragmentTag"] = value
        }

}