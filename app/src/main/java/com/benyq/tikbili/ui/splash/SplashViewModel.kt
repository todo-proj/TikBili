package com.benyq.tikbili.ui.splash

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.api.ApiThrowable
import com.benyq.tikbili.bilibili.BiliBiliConstant
import com.benyq.tikbili.ext.ifTrue
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import com.benyq.tikbili.ui.main.MainPageEvent
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import java.lang.IllegalStateException

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
class SplashViewModel: BaseViewModel() {

    val container by containers<SplashState, SplashEvent>(SplashState(0))

    fun checkLogin() {
        request {
            withTimeout(3000) {
                repository.accountInfo()
            }
        }.onEach {
            container.sendEvent(SplashEvent.ToMainEvent)
        }.catch {
            ((it as? ApiThrowable)?.code == BiliBiliConstant.NOT_LOGIN).ifTrue {
                //未登录，滚去登录
                container.sendEvent(SplashEvent.ToLoginEvent)
                return@catch
            }
            container.sendEvent(SplashEvent.ToMainEvent)
        }.launchIn(viewModelScope)
    }

}