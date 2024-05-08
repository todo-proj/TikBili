package com.benyq.tikbili.player.download

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.benyq.tikbili.player.PlayerConfig
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.player.player.PlayerEvent
import com.benyq.tikbili.player.player.event.ActionPrepare
import com.benyq.tikbili.player.source.MediaSource
import kotlin.math.max

/**
 *
 * @author benyq
 * @date 4/30/2024
 *
 */
class PreloadManager: EventDispatcher.EventListener {

    companion object {
        private var sHandlerThread: HandlerThread? = null
    }

    private val mH: Handler

    init {
        if (sHandlerThread == null) {
            sHandlerThread = HandlerThread("preload")
            sHandlerThread?.start()
        }
        mH = Handler(sHandlerThread!!.looper)
    }

    fun appendItems(items: List<MediaSource>) {
        runInThread {
            items.forEach {
                val l = resolveCacheSize(it)
                it.putExtra(IPreloadTask.EXTRA_PRELOAD_SIZE_IN_BYTES, l)
                CacheLoader.Default.get()?.preload(it)
            }
        }
    }

    private fun cancelPreload(tag: String) {
        runInThread {
            CacheLoader.Default.get()?.stopTask(tag)
        }
    }

    fun cancelAll() {
        runInThread {
            CacheLoader.Default.get()?.stopAllTask()
        }
    }

    override fun onEvent(event: Event) {
        when(event.code) {
            PlayerEvent.Action.PREPARE -> {
                val dataSource = event.cast(ActionPrepare::class.java).mediaSource ?: return
                cancelPreload(dataSource.getUniqueId())
            }
        }
    }

    private fun runInThread(runnable: Runnable) {
        if (Looper.myLooper() == mH.looper) {
            runnable.run()
        } else {
            mH.post(runnable)
        }
    }

    private fun resolveCacheSize(dataSource: MediaSource): Long {
        val l = (1f * PlayerConfig.DEFAULT_PRELOAD_DURATION / dataSource.duration * dataSource.byteSize).toLong()
        return max(l, IPreloadTask.DEFAULT_PRELOAD_SIZE)
    }
}

