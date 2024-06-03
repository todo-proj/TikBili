package com.benyq.tikbili.base.utils

import android.view.View
import java.lang.reflect.Proxy


/**
 *
 * @author benyq
 * @date 5/14/2024
 * hook示例
 */
object ViewClickProxy {

    fun hook(view: View) {
        val method = View::class.java.getDeclaredMethod("getListenerInfo")
        method.isAccessible = true
        val listenerInfo = method.invoke(view) ?: return let {
            L.e(this, "hook listenerInfo == null")
        }
        val fieldClick = listenerInfo.javaClass.getDeclaredField("mOnClickListener")
        fieldClick.isAccessible = true
        val clickObj = fieldClick.get(listenerInfo) ?: return let {
            L.e(this, "hook clickObj == null")
        }
        // 也可以直接调用构造函数
        val proxyListener = Proxy.newProxyInstance(View::class.java.classLoader, arrayOf(View.OnClickListener::class.java)
        ) { proxy, method, args ->
            L.d(this, "hook","hook view click start: ${args?.contentToString()}")
            val res = method?.invoke(clickObj, *args)
            L.d(this, "hook","hook view click end")
            res
        }
        fieldClick.set(listenerInfo, proxyListener)
    }


}

