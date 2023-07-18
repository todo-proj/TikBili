package com.benyq.tikbili

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */

lateinit var appCtx: Application
class TikBiliApp: Application() {

    override fun onCreate() {
        super.onCreate()
        appCtx = this

        MMKV.initialize(this)
    }
}