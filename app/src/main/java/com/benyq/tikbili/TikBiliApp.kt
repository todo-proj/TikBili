package com.benyq.tikbili

import android.app.Application

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
    }
}