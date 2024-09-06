package com.benyq.tikbili.scene.widgets.load

interface LoadMoreAble {

    fun isLoading(): Boolean

    fun finishLoadingMore()

    fun startLoadingMore()

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}