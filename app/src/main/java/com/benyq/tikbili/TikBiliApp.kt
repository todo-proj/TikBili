package com.benyq.tikbili

import android.app.Application
import android.content.Context
import android.util.Log
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.ExoPlayerFactory
import com.benyq.tikbili.player.player.IPlayer
import com.benyq.tikbili.utils.StartLogHelper
import com.tencent.mmkv.MMKV

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */

lateinit var appCtx: Application
class TikBiliApp: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        StartLogHelper.setStart(System.currentTimeMillis())
    }

    override fun onCreate() {
        super.onCreate()
        appCtx = this
        L.ENABLE_LOG = BuildConfig.DEBUG
        MMKV.initialize(this)
        IPlayer.Factory.Default.set(ExoPlayerFactory(this))
        StartLogHelper.getApplicationTime()

        Log.d("TAG", "onCreate: ${resources.displayMetrics.widthPixels}-${resources.displayMetrics.heightPixels}")
    }
}