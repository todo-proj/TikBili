package com.benyq.tikbili.player.download

import com.benyq.tikbili.player.download.IPreloadTask.PreloadState
import com.benyq.tikbili.player.source.MediaSource

/**
 *
 * @author benyq
 * @date 5/6/2024
 *
 */
abstract class BasePreloadTask(): IPreloadTask {

    private var _state: PreloadState = PreloadState.IDLE
    private lateinit var _dataSource: MediaSource

    override fun getState(): PreloadState {
        return _state
    }

    override fun isReleased(): Boolean {
        return _state == PreloadState.RELEASED
    }

    override fun isLoading(): Boolean {
        return _state == PreloadState.LOADING
    }

    override fun isIdle(): Boolean {
        return _state == PreloadState.IDLE
    }

    override fun setState(state: PreloadState) {
        _state = state
    }

    override fun setDataSource(source: MediaSource) {
        this._dataSource = source
    }

    override fun getDataSource(): MediaSource {
        return _dataSource
    }
}