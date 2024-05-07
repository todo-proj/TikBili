package com.benyq.tikbili

import android.app.Application
import android.content.Context
import android.util.Log
import com.benyq.tikbili.base.ext.appCtx
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.ExoPlayerCacheLoader
import com.benyq.tikbili.player.ExoPlayerFactory
import com.benyq.tikbili.player.ExoPlayerPreloadTaskFactory
import com.benyq.tikbili.player.download.CacheLoader
import com.benyq.tikbili.player.download.IPreloadTask
import com.benyq.tikbili.player.player.IPlayer
import com.benyq.tikbili.utils.StartLogHelper
import com.tencent.mmkv.MMKV

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */

class TikBiliApp: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        StartLogHelper.setStart(System.currentTimeMillis())
    }

    override fun onCreate() {
        super.onCreate()
        appCtx = this
        L.ENABLE_LOG = BuildConfig.DEBUG
        L.setTag("TikBili")
        MMKV.initialize(this)
        IPlayer.Factory.Default.set(ExoPlayerFactory(this))
        CacheLoader.Default.set(ExoPlayerCacheLoader(this, ExoPlayerPreloadTaskFactory(this)))
        StartLogHelper.getApplicationTime()
    }
}