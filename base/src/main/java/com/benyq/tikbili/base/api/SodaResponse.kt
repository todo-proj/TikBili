package com.benyq.tikbili.base.api

interface SodaResponse<T> {

    fun isSuccess(): Boolean

    fun getMessage(): String

    fun getErrorCode(): Int

    fun getRealData(): T?


}