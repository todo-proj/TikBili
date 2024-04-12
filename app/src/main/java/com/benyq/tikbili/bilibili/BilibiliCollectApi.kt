package com.benyq.tikbili.bilibili

import com.benyq.tikbili.bilibili.model.AccountModel
import com.benyq.tikbili.bilibili.model.BiliBiliResponse
import com.benyq.tikbili.bilibili.model.RecommendVideoData
import com.benyq.tikbili.bilibili.model.VideoDetailModel
import com.benyq.tikbili.bilibili.model.VideoReplyModel
import com.benyq.tikbili.bilibili.model.VideoUrlModel
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author benyq
 * @date 7/17/2023
 *  B站API，https://github.com/SocialSisterYi/bilibili-API-collect
 */
interface BilibiliCollectApi {

    companion object {
        const val BASE_URL = "https://api.bilibili.com/"
    }

    @GET("x/web-interface/index/top/rcmd")
    suspend fun getRecommend(@Query("fresh_type") freshType: Int = 3, @Query("ps") pageSize: Int): BiliBiliResponse<RecommendVideoData>

    @GET("x/web-interface/view")
    suspend fun videoInfo(@Query("bvid") bvid: String): BiliBiliResponse<VideoDetailModel>

    @GET("x/player/playurl")
    suspend fun videoUrl(@Query("bvid")bvid: String, @Query("cid") cid: String, @Query("qn") qn: String): BiliBiliResponse<VideoUrlModel>

    @GET("x/member/web/account")
    suspend fun accountInfo(): BiliBiliResponse<AccountModel>

    @GET("x/v2/reply")
    suspend fun videoReply(@Query("oid") oid: String, @Query("pn") pn: Int, @Query("type") type: Int = 1, @Query("mode") mode: Int = 3, @Query("ps") ps: Int = 20): BiliBiliResponse<VideoReplyModel>
}