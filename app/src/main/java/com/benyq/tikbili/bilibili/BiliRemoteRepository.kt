package com.benyq.tikbili.bilibili

/**
 *
 * @author benyq
 * @date 7/17/2023
 * B站api
 */
class BiliRemoteRepository {

    private val api = RetrofitManager.bilibiliApi

    suspend fun getRecommend(pageSize: Int = 10) = api.getRecommend(pageSize = pageSize)

    suspend fun videoInfo(bvid: String) = api.videoInfo(bvid)

    suspend fun videoUrl(bvid: String, cid: String, qn: String = "80") = api.videoUrl(bvid, cid, qn)

    //这个接口可以用来检测是否登录
    suspend fun accountInfo() = api.accountInfo()
    suspend fun videoReply(oid: String, pn: Int) = api.videoReply(oid, pn)
}