package com.benyq.tikbili

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benyq.tikbili.api.RetrofitManager
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
@RunWith(AndroidJUnit4::class)
class BilibiliApiTest {
    @Test
    fun useAppContext() {
        val api = RetrofitManager.bilibiliApi
        runBlocking {
            val response = api.videoUrl("BV1JN411D74V", "1173661503", "80")
            println(response.code)
        }
    }
}