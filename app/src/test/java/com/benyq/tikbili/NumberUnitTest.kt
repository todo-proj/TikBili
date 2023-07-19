package com.benyq.tikbili

import org.junit.Assert
import org.junit.Test

/**
 *
 * @author benyq
 * @date 7/19/2023
 *
 */
class NumberUnitTest {
    @Test
    fun format_isCorrect() {
        val num = 13456
        val result = "1.3万"
        Assert.assertEquals(result, "%.1f万".format(num.toFloat() / 10000))
    }
}