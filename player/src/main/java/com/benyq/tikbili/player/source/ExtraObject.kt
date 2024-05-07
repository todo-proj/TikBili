package com.benyq.tikbili.player.source

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Collections

/**
 *
 * @author benyq
 * @date 4/25/2024
 *
 */
open class ExtraObject: Serializable {
    private val _extras = Collections.synchronizedMap(LinkedHashMap<String, Any>())

    fun <T> getExtra(key: String, clazz: Class<T>): T? {
        val extra = _extras[key] ?: return null
        if (clazz.isInstance(extra)) {
            return clazz.cast(extra)
        }else {
            throw ClassCastException(extra.javaClass.toString() + " can't be cast to " + clazz)
        }
    }

    fun putExtra(key: String, extra: Any?) {
        if (extra == null) {
            _extras.remove(key)
        }else {
            if (extra is Serializable || extra is Parcelable) {
                _extras[key] = extra
            } else {
                throw IllegalArgumentException("Unsupported type " + extra.javaClass)
            }
        }
    }

    fun clearExtras() {
        _extras.clear()
    }
}