package com.benyq.tikbili.base.utils

import android.util.Log

/**
 *
 * @author benyq
 * @date 4/11/2024
 *
 */
object L {
    private const val TAG = "Player_Kit"
    var ENABLE_LOG = false

    private fun obj2String(o: Any?): String? {
        return when {
            o == null -> null
            o is String -> o
            o is Number -> o.toString()
            o is Boolean -> o.toString()
            o.javaClass.isAnonymousClass -> {
                val s = o.toString()
                s.substring(s.lastIndexOf('.'))
            }
            o is Class<*> -> o.simpleName
            else -> o.javaClass.simpleName +'@'+Integer.toHexString(o.hashCode())
        }
    }

    fun v(o: Any, method: String, vararg args: Any) {
        if (ENABLE_LOG) {
            Log.v(TAG, createLog(o, method, *args))
        }
    }

    fun d(o: Any, method: String, vararg args: Any?) {
        if (ENABLE_LOG) {
            Log.d(TAG, createLog(o, method, *args))
        }
    }

    fun w(o: Any, method: String, vararg args: Any?) {
        if (ENABLE_LOG) {
            Log.w(TAG, createLog(o, method, *args))
        }
    }

    fun e(o: Any, method: String, vararg args: Any?) {
        if (ENABLE_LOG) {
            Log.e(TAG, createLog(o, method, *args))
        }
    }

    private fun createLog(o: Any, method: String, vararg args: Any?): String {
        val msg = StringBuilder("[" + obj2String(o) + "]").append(" -> ").append(method)
        for (arg in args) {
            msg.append(" -> ").append(obj2String(arg))
        }
        return msg.toString()
    }
}