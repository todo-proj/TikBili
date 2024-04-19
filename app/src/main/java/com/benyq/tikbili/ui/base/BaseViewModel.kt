package com.benyq.tikbili.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.benyq.tikbili.base.api.ApiThrowable
import com.benyq.tikbili.base.api.SodaResponse
import com.benyq.tikbili.base.coroutine.Coroutine
import com.benyq.tikbili.base.ui.DataState
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.bilibili.model.BiliBiliResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    fun <T> request(action: suspend () -> BiliBiliResponse<T>) = flow {
        val response = action()
        if (response.isSuccess()) {
            emit(response.getRealData())
        }else {
            throw ApiThrowable(response.getErrorCode(), response.getMessage())
        }
    }.flowOn(Dispatchers.IO)


    protected fun <T> execute(
        scope: CoroutineScope = viewModelScope,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        executeContext: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.() -> T,
    ): Coroutine<T> {
        return Coroutine.async(scope, context, start, executeContext, block)
    }

    protected fun <R> submit(
        scope: CoroutineScope = viewModelScope,
        context: CoroutineContext = Dispatchers.IO,
        block: suspend CoroutineScope.() -> Deferred<R>,
    ): Coroutine<R> {
        return Coroutine.async(scope, context) { block().await() }
    }


    protected fun <T> flowResponse(
        defaultValue: T? = null,
        request: suspend () -> SodaResponse<T>,
    ): Flow<DataState<T>> {
        return flow {
            val response = request()
            if (response.isSuccess() || defaultValue != null) {
                emit(DataState.Success<T>(response.getRealData() ?: defaultValue!!))
            } else {
                emit(DataState.Error<T>(ApiThrowable(response.getErrorCode(), response.getMessage())))
            }
        }.flowOn(Dispatchers.IO)
            .onStart { emit(DataState.Loading(true)) }
            .catch {
                emit(DataState.Error<T>(it))
            }
            .onCompletion {
                emit(DataState.Loading(false))
            }
    }

    protected fun <T> executeFlow(request: suspend () -> T): Flow<DataState<T>> {
        return flow<DataState<T>> {
            val response = request()
            emit(DataState.Success(response))
        }.flowOn(Dispatchers.IO)
            .onStart { emit(DataState.Loading(true)) }
            .catch { DataState.Error<T>(it) }
            .onCompletion {
                emit(DataState.Loading(false))
            }
    }

}