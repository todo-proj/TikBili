package com.benyq.tikbili.utils

import java.util.Calendar

/**
 *
 * @author benyq
 * @date 4/17/2024
 *
 */
object DateUtils {

    private val MILLiSECOND_OF_ONE_DAY = 86400_000
    private val MILLiSECOND_OF_ONE_HOUR = 3600_000
    private val MILLiSECOND_OF_ONE_MINUTE = 60_000

    fun formatTime(timestamp: Long): String {
        val current = getDateModel(System.currentTimeMillis())
        val inDate = getDateModel(timestamp * 1000)

        val cTime = inDate.timeInMillis

        if (current.timeInMillis - cTime < MILLiSECOND_OF_ONE_HOUR) {
            val minute = (current.timeInMillis - cTime) / MILLiSECOND_OF_ONE_MINUTE
            return "${minute}分钟前"
        }
        if (current.timeInMillis - cTime < MILLiSECOND_OF_ONE_DAY) {
            if (current.day != inDate.day) {
                return "昨天 ${inDate.hour}-${inDate.minute}"
            }
            val hour = (current.timeInMillis - cTime) / MILLiSECOND_OF_ONE_HOUR
            return "${hour}小时前"
        }
        if (current.timeInMillis - cTime < MILLiSECOND_OF_ONE_DAY * 7) {
            return "${current.day - inDate.day}天前"
        }
        if (current.year == inDate.year) {
            return "${inDate.month}-${inDate.day}"
        }
        return "${inDate.year}-${inDate.month}-${inDate.day}"
    }

    /**
     * @param [cTime] 毫秒级时间戳
     */
    private fun getDateModel(cTime: Long): DateModel {
        val calender = Calendar.getInstance()
        calender.timeInMillis = cTime
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        return DateModel(year, month, day, hour, minute, cTime)
    }
}

private data class DateModel(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val timeInMillis: Long
)