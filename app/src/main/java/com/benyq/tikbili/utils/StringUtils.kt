package com.benyq.tikbili.utils

/**
 *
 * @author benyq
 * @date 4/12/2024
 *
 */
object StringUtils {

    fun num2String(num: Int): String {
        if (num < 10000) return "$num"
        return "%.1f万".format((num.toFloat() / 10000))
    }

}