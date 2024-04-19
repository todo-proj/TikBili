package com.benyq.tikbili.bilibili.model

import com.benyq.tikbili.base.api.SodaResponse

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
data class BiliBiliResponse<T>(
    private val code: Int,
    private val message: String,
    private val data: T,
): SodaResponse<T>{
    override fun isSuccess(): Boolean {
        return code == 0
    }

    override fun getMessage() = message

    override fun getErrorCode() = code

    override fun getRealData(): T = data

}