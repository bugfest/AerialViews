package com.neilturner.aerialviews.utils

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.neilturner.aerialviews.models.videos.Apple2019Video
import java.util.*

object JsonHelper {

    private val jsonParser = Gson()

    fun <T> parseJson(context: Context, @RawRes res: Int, tClass: Class<T>?): T {
        val stream = context.resources.openRawResource(res)
        val json = Scanner(stream).useDelimiter("\\A").next()
        return jsonParser.fromJson(json, tClass)
    }

    class Wrapper {
        val assets: List<Apple2019Video>? = null
    }
}