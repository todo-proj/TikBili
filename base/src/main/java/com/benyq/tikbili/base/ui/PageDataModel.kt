package com.benyq.tikbili.base.ui

/**
 *
 * @author benyq
 * @date 4/24/2024
 *
 */
data class PageDataModel<T>(
    val data: T,
    val isFirst: Boolean,
    val isEnd: Boolean
)