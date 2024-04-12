package com.benyq.tikbili.player.utils

import java.util.Formatter

/**
 *
 * @author benyq
 * @date 4/10/2024
 *
 */
object TimeUtils {

    private val _stringBuilder by lazy { StringBuilder() }
    private val _format by lazy { Formatter(_stringBuilder) }


    // 格式化时间 timeMS -> HH:MM:SS
    @JvmStatic
    fun time2String(time: Long): String {
        val totalSecond = time / 1000
        val seconds = totalSecond % 60
        val minutes = (totalSecond / 60) % 60
        val hours = totalSecond / 3600

        _stringBuilder.setLength(0)
        return if (hours > 0) {
            _format.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            _format.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}