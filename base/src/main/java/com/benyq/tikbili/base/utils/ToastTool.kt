package com.benyq.tikbili.base.utils

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import com.benyq.tikbili.base.ext.appCtx

object ToastTool {

    private var _toast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    fun show(msg: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            realShow(msg)
        }else {
            mainHandler.post {
                realShow(msg)
            }
        }
    }

    fun show(@StringRes resId: Int) {
        show(appCtx.getString(resId))
    }

    private fun realShow(msg: String) {
        if (_toast == null) {
            _toast = Toast.makeText(appCtx, msg, Toast.LENGTH_SHORT)
            _toast?.setGravity(Gravity.CENTER, 0, 0)
        }else {
            _toast?.cancel()
        }
        _toast?.apply {
            setText(msg)
            show()
        }
    }

}