package com.benyq.tikbili.bilibili.model

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
data class BiliBiliResponse<T>(
    val code: Int,
    val message: String,
    val data: T,
)