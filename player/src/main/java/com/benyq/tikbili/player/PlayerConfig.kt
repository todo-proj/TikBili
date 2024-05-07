package com.benyq.tikbili.player

/**
 *
 * @author benyq
 * @date 4/9/2024
 *
 */
object PlayerConfig {
    @JvmField
    var EVENT_POOL_ENABLE = true
    @JvmField
    var EVENT_POOL_SIZE = 5

    // 预加载时长
    const val DEFAULT_PRELOAD_DURATION = 2000L

}