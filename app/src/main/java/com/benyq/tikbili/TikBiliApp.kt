package com.benyq.tikbili

import android.app.Application
import android.content.Context
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
        MMKV.initialize(this)
        StartLogHelper.getApplicationTime()
    }
}