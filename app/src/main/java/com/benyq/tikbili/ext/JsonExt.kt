package com.benyq.tikbili.ext

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */

fun String.isJson(): Boolean {
    return try {
        val jsonElement = JsonParser.parseString(this)
        jsonElement.isJsonObject
    }catch (e: JsonSyntaxException){
        false
    }
}