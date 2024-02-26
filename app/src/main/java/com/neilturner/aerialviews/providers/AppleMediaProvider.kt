package com.neilturner.aerialviews.providers

import android.content.Context
import android.util.Log
import com.neilturner.aerialviews.R
import com.neilturner.aerialviews.models.prefs.AppleVideoPrefs
import com.neilturner.aerialviews.models.videos.AerialMedia
import com.neilturner.aerialviews.models.videos.VideoMetadata
import com.neilturner.aerialviews.utils.JsonHelper
import com.neilturner.aerialviews.utils.JsonHelper.parseJson
import com.neilturner.aerialviews.utils.JsonHelper.parseJsonMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppleMediaProvider(context: Context, private val prefs: AppleVideoPrefs) : MediaProvider(context) {

    override val enabled: Boolean
        get() = prefs.enabled

    override suspend fun fetchMedia(): List<AerialMedia> {
        return fetchAppleVideos().first
    }

    override suspend fun fetchTest(): String {
        return fetchAppleVideos().second
    }

    override suspend fun fetchMetadata(): List<VideoMetadata> = withContext(Dispatchers.IO) {
        val metadata = mutableListOf<VideoMetadata>()
        val strings = parseJsonMap(context, R.raw.tvos15_strings)
        val wrapper = parseJson(context, R.raw.tvos15, JsonHelper.Apple2018Videos::class.java)
        wrapper.assets?.forEach {
            val video = VideoMetadata(
                it.allUrls(),
                it.location,
                it.pointsOfInterest.mapValues { poi ->
                    strings[poi.value] ?: it.location
                }
            )
            metadata.add(video)
        }
        return@withContext metadata
    }

    private suspend fun fetchAppleVideos(): Pair<List<AerialMedia>, String> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<AerialMedia>()
        val quality = prefs.quality
        val wrapper = parseJson(context, R.raw.tvos15, JsonHelper.Apple2018Videos::class.java)
        wrapper.assets?.forEach {
            videos.add(
                AerialMedia(
                    it.uriAtQuality(quality)
                )
            )
        }

        Log.i(TAG, "${videos.count()} $quality videos found")
        return@withContext Pair(videos, "")
    }

    companion object {
        private const val TAG = "AppleVideoProvider"
    }
}
