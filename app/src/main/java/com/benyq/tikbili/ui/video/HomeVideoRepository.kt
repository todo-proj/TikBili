package com.benyq.tikbili.ui.video

import com.benyq.tikbili.appCtx
import com.benyq.tikbili.bilibili.model.RecommendVideoModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HomeVideoRepository {

    fun loadHomeVideo(): Flow<Result<List<RecommendVideoModel>>> {
        return flow {
            val json = appCtx.assets.open("bilibili/recommedn_vides.json")
                .bufferedReader().readText()
            val gson = Gson()
            val jsonElement = JsonParser.parseString(json)
            val elements = jsonElement.asJsonObject.get("item")
            val data: List<RecommendVideoModel> =
                gson.fromJson(elements, object : TypeToken<List<RecommendVideoModel>>() {}.type)
            emit(Result.success(data))
        }.flowOn(Dispatchers.IO)
            .catch {
                emit(Result.failure(it))
            }
    }

}