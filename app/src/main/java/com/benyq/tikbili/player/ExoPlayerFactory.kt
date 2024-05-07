package com.benyq.tikbili.player

import android.content.Context
import android.net.Uri
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.player.player.IPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheWriter

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
class ExoPlayerFactory(private val context: Context): IPlayer.Factory {
    override fun create(source: MediaSource): IPlayer {
        val player = ExoVideoPlayer(context)
        player.create()
        return player
    }
}