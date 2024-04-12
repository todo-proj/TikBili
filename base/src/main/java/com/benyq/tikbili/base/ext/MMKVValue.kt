package com.benyq.tikbili.base.ext

import android.util.Log
import com.tencent.mmkv.MMKV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.reflect.KProperty

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */
class MMKVValue<T>(val name: String, private val default: T) {

    companion object {
        private var kv = MMKV.defaultMMKV()

        fun removeKey(key: String) {
            kv.removeValueForKey(key)
        }

        fun clearAll() {
            kv.clearAll()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return try {
            decode(name, default)
        }catch (e: Throwable) {
            e.printStackTrace()
            Log.e("MMKVValue", "getValue: ${e.message}")
            default
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        try {
            encode(name, value)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("MMKVValue", "setValue: ${e.message}")
        }
    }

    private fun encode(key: String, value: T): Boolean {
        return when (value) {
            is String -> kv.encode(key, value)
            is Float -> kv.encode(key, value)
            is Boolean -> kv.encode(key, value)
            is Int -> kv.encode(key, value)
            is Long -> kv.encode(key, value)
            is Double -> kv.encode(key, value)
            is ByteArray -> kv.encode(key, value)
            else -> kv.encode(name, serialize(value))
        }
    }

    private fun decode(name: String, default: T): T = with(kv) {
        val res: Any = when (default) {
            is Long -> decodeLong(name, default)
            is String -> decodeString(name, default) ?: ""
            is Int -> decodeInt(name, default)
            is Boolean -> decodeBool(name, default)
            is Float -> decodeFloat(name, default)
            else -> deSerialization(decodeString(name, serialize(default)) ?: "")
        }
        return res as T
    }

    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


}