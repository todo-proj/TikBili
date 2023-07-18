package com.benyq.tikbili.ui.base

import androidx.lifecycle.ViewModel
import com.benyq.tikbili.api.ApiThrowable
import com.benyq.tikbili.bilibili.BiliRemoteRepository
import com.benyq.tikbili.bilibili.model.BiliBiliResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
open class BaseViewModel : ViewModel() {

    protected val repository = BiliRemoteRepository()

    fun <T> request(action: suspend () -> BiliBiliResponse<T>) = flow {
        val response = action()
        if (response.code == 0) {
            emit(response.data)
        }else {
            throw ApiThrowable(response.code, response.message)
        }
    }.flowOn(Dispatchers.IO)

}