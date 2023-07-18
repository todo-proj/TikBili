package com.benyq.tikbili.bilibili

import com.benyq.tikbili.api.RetrofitManager
import com.benyq.tikbili.bilibili.model.BiliBiliResponse

/**
 *
 * @author benyq
 * @date 7/17/2023
 * B站api
 */
class BiliRemoteRepository {

    private val api = RetrofitManager.bilibiliApi

    suspend fun getRecommend() = api.getRecommend()

    suspend fun videoInfo(bvid: String) = api.videoInfo(bvid)

    suspend fun videoUrl(bvid: String, cid: String, qn: String = "80") = api.videoUrl(bvid, cid, qn)

    //这个接口可以用来检测是否登录
    suspend fun accountInfo() = api.accountInfo()
}