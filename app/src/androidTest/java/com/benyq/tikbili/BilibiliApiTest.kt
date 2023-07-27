package com.benyq.tikbili

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benyq.tikbili.api.RetrofitManager
import com.benyq.tikbili.bilibili.BilibiliCollectApi
import com.benyq.tikbili.bilibili.model.BiliBiliResponse
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.benyq.tikbili.bilibili.model.VideoUrlModel
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
    val api = RetrofitManager.bilibiliApi
    @Test
    fun useAppContext() {
        runBlocking {
            val response = videoReplay()
            println(response.code)
        }
    }

    private suspend fun videoUrl(): BiliBiliResponse<VideoUrlModel> {
        return api.videoUrl("BV1JN411D74V", "1173661503", "80")
    }

    private suspend fun videoReplay(): BiliBiliResponse<VideoReplyModel> {
        return api.videoReply("870698985", 1)
    }


}