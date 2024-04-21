package com.benyq.tikbili.player.playback

import android.os.Looper
import android.util.Log
import android.view.Surface
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.playback.event.StateBindPlayer
import com.benyq.tikbili.player.player.IPlayer
import com.benyq.tikbili.player.player.IPlayerPool
import java.lang.ref.WeakReference


/**
 *
 * @author benyq
 * @date 4/7/2024
 * player 和 VideoView 的交互与事件分发
 */
class PlaybackController(
    private val playerPool: IPlayerPool = IPlayerPool.DEFAULT,
    private val playerFactory: IPlayer.Factory = IPlayer.Factory.Default.get(),
) {

    private val tag = "PlaybackController"
    private var _videoView: VideoView? = null
    private var _player: IPlayer? = null
    private val _dispatcher = EventDispatcher(Looper.getMainLooper())
    private val _playEventListener = PlaybackEventListener(this)
    private val _surfaceListener = SurfaceListener(this)
    private var startOnReadyCommand: Runnable? = null

    fun bind(videoView: VideoView) {
        if (_videoView != null && videoView != _videoView) {
            unbindVideoView()
        }
        bindVideoView(videoView)
    }

    fun unbind(videoView: VideoView) {
        unbindPlayer(false)
        unbindVideoView()
    }

    private fun bindVideoView(newVideoView: VideoView?) {
        if (_videoView == null && newVideoView != null) {
            _videoView = newVideoView
            newVideoView.addVideoViewListener(_surfaceListener)
            newVideoView.bindController(this)
        }

    }

    private fun unbindVideoView() {
        _videoView?.removeVideoViewListener(_surfaceListener)
        _videoView?.unbindController(this)
        _videoView = null
    }


    private fun bindPlayer(newPlayer: IPlayer?) {
        if (_player == null && newPlayer != null && !newPlayer.isReleased()) {
            _player = newPlayer
            newPlayer.addPlayerListener(_playEventListener)
            _dispatcher.obtain(StateBindPlayer::class.java).dispatch()
        }
    }

    fun unbindPlayer(recycle: Boolean = true) {
        startOnReadyCommand = null
        _player?.let {
            if (recycle) {
                it.setSurface(null)
                playerPool.recycle(it)
            }
            it.removePlayerListener(_playEventListener)
            _player = null
        }
    }

    fun addPlaybackListener(listener: EventDispatcher.EventListener) {
        _dispatcher.addSubscriber(listener)
    }

    fun removePlaybackListener(listener: EventDispatcher.EventListener) {
        _dispatcher.removeSubscriber(listener)
    }

    fun removeAllPlaybackListener() {
        _dispatcher.removeAllListeners()
    }

    fun dispatcher(): EventDispatcher {
        return _dispatcher
    }

    fun startPlayback(startWhenPrepared: Boolean = true) {
        startOnReadyCommand = null
        val attachedVideoView = _videoView ?: let {
            Log.e(tag, "startPlayback: videoView == null")
            return
        }
        val mediaSource = attachedVideoView.dataSource() ?: let {
            Log.e(tag, "startPlayback: mediaSource == null")
            return
        }
        attachedVideoView.setReuseSurface(true)
        _player?.let {
            if (it.isReleased()) {
                unbindPlayer()
            } else if (!it.isIDLE() && mediaSource != it.getDataSource()) {
                unbindPlayer()
            }
        }
        val attachedPlayer = _player ?: let {
            val player = playerPool.acquire(mediaSource, playerFactory)
            bindPlayer(player)
            player
        }
        // 准备播放
        // renderView 需要准备好
        if (isReady(_videoView)) {
            startPlayback(startWhenPrepared, attachedPlayer, attachedVideoView)
        }else {
            val surface = videoView()?.surface()
            L.d(
                this, if (startWhenPrepared) "startPlayback" else "preparePlayback",
                "but resource not ready",
                attachedPlayer,  // player not bind
                attachedVideoView,  // view not bind
                surface,  // surface not ready
                surface // data source not bind
            )
            L.w(this, "startPlayback", surface)
            // 等待回调启动
            startOnReadyCommand = Runnable {
                if (isReady(_videoView)) {
                    startPlayback(startWhenPrepared, attachedPlayer, attachedVideoView)
                }
            }
        }
    }

    private fun startPlayback(startWhenPrepared: Boolean, player: IPlayer, videoView: VideoView) {
        val dataSource = videoView.dataSource() ?: return
        val surface = videoView.surface() ?: return
        player.setSurface(surface)
        when (player.getState()) {
            IPlayer.PlayState.STATE_IDLE -> {
                player.setStartWhenPrepared(startWhenPrepared)
                player.setDataSource(dataSource)
                player.prepare()
            }

            IPlayer.PlayState.STATE_PREPARED, IPlayer.PlayState.STATE_PAUSED, IPlayer.PlayState.STATE_COMPLETED -> {
                if (startWhenPrepared) {
                    player.start()
                }
            }

            IPlayer.PlayState.STATE_STARTED -> {

            }

            else -> {
            }
        }
    }

    fun pausePlayback() {
        _player?.let {
            it.pause()
            it.setStartWhenPrepared(false)
        }
    }

    fun stopPlayback() {
        _videoView?.setReuseSurface(false)
        startOnReadyCommand = null
        unbindPlayer()
    }

    fun player(): IPlayer? {
        return _player
    }

    fun videoView(): VideoView? {
        return _videoView
    }

    private fun isReady(videoView: VideoView?): Boolean {
        if (videoView == null) return false
        val surface = videoView.surface() ?: return false
        return videoView.dataSource() != null && surface.isValid
    }

    class PlaybackEventListener(playbackController: PlaybackController): EventDispatcher.EventListener {

        private val _ref = WeakReference(playbackController)

        override fun onEvent(event: Event) {
            val controller = _ref.get() ?: return
            controller._dispatcher.dispatchEvent(event)
        }

    }


    class SurfaceListener(playbackController: PlaybackController): VideoView.VideoViewListener {

        private val _weakRef = WeakReference(playbackController)

        override fun onSurfaceAvailable(surface: Surface, width: Int, height: Int) {
            val playbackController = _weakRef.get() ?: return
            val startCommand = playbackController.startOnReadyCommand
            if (startCommand != null) {
                startCommand.run()
            }else {
                playbackController.player()?.setSurface(surface)
            }
        }

        override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
            if (hasWindowFocus) {
                val playbackController = _weakRef.get() ?: return
                playbackController.player()?.setSurface(playbackController.videoView()?.surface())
            }
        }

    }
}