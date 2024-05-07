package com.benyq.tikbili.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

object MediaCacheFactory {
    private const val TAG = "MediaCacheFactory"
    private var cacheFactory: CacheDataSource.Factory? = null

    @Synchronized
    fun getCacheFactory(ctx: Context): CacheDataSource.Factory {
        if (cacheFactory == null) {
            val downDirectory = File(ctx.cacheDir, "videos")
            val cache = SimpleCache(
                downDirectory,
                LeastRecentlyUsedCacheEvictor(1024 * 1024 * 512),
                StandaloneDatabaseProvider(ctx)
            )
            cacheFactory = CacheDataSource.Factory().setCache(cache)
                .setCacheReadDataSourceFactory(
                    DefaultDataSource.Factory(
                        ctx,
                        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(false)
                            .setConnectTimeoutMs(8000)
                            .setReadTimeoutMs(8000)
                            .setUserAgent("MY_Exoplayer")
                    )
                )
                .setUpstreamDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                         // B站防盗链
                        .setDefaultRequestProperties(mutableMapOf("referer" to "https://www.bilibili.com"))
                        .setAllowCrossProtocolRedirects(false)
                        .setConnectTimeoutMs(8000)
                        .setReadTimeoutMs(8000)
                        .setUserAgent("MY_Exoplayer")
                )
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setEventListener(object : CacheDataSource.EventListener {
                    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                        Log.d(TAG, "onCachedBytesRead $cacheSizeBytes  >> $cachedBytesRead")
                    }

                    override fun onCacheIgnored(reason: Int) {
                        Log.d(TAG, "onCacheIgnored $reason")
                    }

                })
        }
        return cacheFactory!!
    }
}
