package com.benyq.tikbili.player

import android.content.Context
import com.benyq.tikbili.player.source.MediaSource
import com.benyq.tikbili.player.player.IPlayer

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