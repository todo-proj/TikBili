package com.benyq.tikbili.scene.widgets.load.impl

import androidx.viewpager2.widget.ViewPager2
import com.benyq.tikbili.scene.widgets.load.LoadMoreAble

class ViewPager2LoadMoreHelper(private val viewPager: ViewPager2,
                               private val preloadSize: Int = 1,
                               private val onLoadMore: () -> Unit): LoadMoreAble {
    private var isLoading = false

    init {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val itemCount = viewPager.adapter?.itemCount ?: 0
                if ((itemCount - position - 1 <= preloadSize) && !isLoading()) {
                    onLoadMore()
                }
            }
        })
    }

    override fun isLoading(): Boolean {
        return isLoading
    }

    override fun finishLoadingMore() {
        isLoading = false
    }

    override fun startLoadingMore() {
        isLoading = true
    }

}