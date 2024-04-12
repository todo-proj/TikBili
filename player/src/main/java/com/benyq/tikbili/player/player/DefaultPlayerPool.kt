package com.benyq.tikbili.player.player

import com.benyq.tikbili.player.source.MediaSource
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author benyq
 * @date 4/8/2024
 *
 */
class DefaultPlayerPool: IPlayerPool {


    private val _playerMap = ConcurrentHashMap<String, IPlayer>()

    override fun acquire(mediaSource: MediaSource, factory: IPlayer.Factory): IPlayer {
        var player = get(mediaSource)
        if (player != null) {
            if (player.isReleased()) {
                recycle(player)
            }
        }
        if (player == null) {
            player = create(mediaSource, factory)
        }
        return player
    }

    override fun get(mediaSource: MediaSource): IPlayer? {
        return _playerMap[key(mediaSource)]
    }

    override fun recycle(player: IPlayer) {
        _playerMap.forEach { (k, v) ->
            if (v == player) {
                _playerMap.remove(k)
                return@forEach
            }
        }
        player.release()
    }

    private fun create(mediaSource: MediaSource, factory: IPlayer.Factory): IPlayer {
        val player = factory.create(mediaSource)
        _playerMap[key(mediaSource)] = player
        // TODO 也许会做一个初始化配置？
        return player
    }


    private fun key(mediaSource: MediaSource): String {
        return mediaSource.getUniqueId()
    }
}