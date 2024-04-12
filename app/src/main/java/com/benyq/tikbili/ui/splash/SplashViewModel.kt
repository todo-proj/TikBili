package com.benyq.tikbili.ui.splash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.api.ApiThrowable
import com.benyq.tikbili.bilibili.BiliBiliConstant
import com.benyq.tikbili.base.ext.ifTrue
import com.benyq.tikbili.ui.base.BaseViewModel
import com.benyq.tikbili.ui.base.mvi.extension.containers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
class SplashViewModel(context: Application): BaseViewModel(context) {

    val container by containers<SplashState, SplashEvent>(SplashState(0))

    fun checkLogin() {
        request {
            withTimeout(2500) {
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