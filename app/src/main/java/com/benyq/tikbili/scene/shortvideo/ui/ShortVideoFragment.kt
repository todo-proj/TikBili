package com.benyq.tikbili.scene.shortvideo.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ui.DataState
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.base.utils.ToastTool
import com.benyq.tikbili.databinding.FragmentShortVideoBinding
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.scene.horizontal.HorizontalVideoActivity
import com.benyq.tikbili.scene.SceneEvent
import com.benyq.tikbili.scene.horizontal.HorizontalFrameHolder
import com.benyq.tikbili.scene.shortvideo.event.ActionCommentVisible
import com.benyq.tikbili.scene.shortvideo.ui.comment.ShortVideoCommentView
import com.benyq.tikbili.scene.widgets.load.LoadMoreAble
import com.benyq.tikbili.ui.base.BaseFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 *
 * @author benyq
 * @date 4/16/2024
 *
 */
class ShortVideoFragment : BaseFragment<FragmentShortVideoBinding>(R.layout.fragment_short_video) {

    private val viewModel by viewModels<ShortVideoViewModel>()
    private var currentVid: String = ""

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(dataBind.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupShortVideoPage()
        lifecycleScope.launch {
            viewModel.commentEvent.collect {
                when (it) {
                    is DataState.Loading -> {
                        if (it.loading) {
                            dataBind.commentLayout.showLoading()
                        }else {
                            dataBind.commentLayout.dismissLoading()
                        }
                    }

                    is DataState.Success -> {
                        if (it.data.isFirst) {
                            dataBind.commentLayout.submitComment(it.data.data)
                        }else {
                            dataBind.commentLayout.appendComment(it.data.data, it.data.isEnd)
                        }
                    }

                    is DataState.Error -> {
                        dataBind.commentLayout.failLoad(it.error)
                        L.e(this@ShortVideoFragment, "commentEvent collect", "error", it.error?.message)
                    }
                }
            }
        }
        dataBind.commentLayout.setOnCommentEventListener(object : ShortVideoCommentView.OnCommentEventListener {

            override fun loadMoreComments() {
                viewModel.queryVideoReply(currentVid)
            }

            override fun loadReplyComments(id: String) {

            }

            override fun changedState(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dataBind.shortPage.controller().dispatcher().obtain(ActionCommentVisible::class.java).init(false).dispatch()
                }
            }

            override fun changedOffset(bottomSheet: View, slideOffset: Float) {
                val fullHeight = dataBind.flContainer.height - (dataBind.flContainer.paddingTop + dataBind.flContainer.paddingBottom)
                val offset = abs(slideOffset)
                val newHeight = fullHeight - (1 - offset) * bottomSheet.height
                dataBind.shortPage.updateLayoutParams {
                    height = newHeight.toInt()
                }
            }

        })
        viewModel.getRecommend()
    }

    private fun setupShortVideoPage() {
        val shortPageView = dataBind.shortPage
        shortPageView.setLifeCycle(lifecycle)
        lifecycleScope.launch {
            viewModel.videoEvent.collect {
                when (it) {
                    is DataState.Loading -> {
                        dataBind.loading.isVisible = it.loading && shortPageView.isEmpty()
                    }

                    is DataState.Success -> {
                        val data = it.data
                        if (data.isFirst) {
                            shortPageView.setItems(data.data)
                        }else {
                            shortPageView.appendItems(data.data)
                            shortPageView.finishLoadingMore()
                        }
                    }

                    is DataState.Error -> {
                        shortPageView.finishLoadingMore()
                        ToastTool.show(it.message)
                    }
                }
            }
        }
        shortPageView.controller().addPlaybackListener(object : EventDispatcher.EventListener {
            override fun onEvent(event: Event) {
                when (event.code) {
                    SceneEvent.Action.COMMENT -> {
                        L.d(this@ShortVideoFragment, "onEvent", event.code)
                        viewModel.queryVideoReply(currentVid, true)
                        shortPageView.controller().dispatcher().obtain(ActionCommentVisible::class.java).init(true).dispatch()
                        dataBind.commentLayout.showComment()
                    }
                    SceneEvent.Action.THUMB_UP -> {}
                    SceneEvent.Action.FULLSCREEN -> {
                        val data = shortPageView.controller().player()?.getDataSource() ?: return
                        val player = shortPageView.controller().player() ?: return
                        val bitmap = shortPageView.controller().videoView()?.currentFrame()
                        HorizontalFrameHolder.set(bitmap)
                        HorizontalVideoActivity.startActivity(requireActivity(), data, player.isPlaying(), player.getCurrentPosition())
                        requireActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation_alpha)
                    }
                }
            }
        })
        shortPageView.setOnPageChangeCallback {
            dataBind.commentLayout.resetComment()
            val currentItem = shortPageView.currentItem() ?: return@setOnPageChangeCallback
            currentVid = currentItem.id
        }

        shortPageView.setOnLoadMoreListener(object : LoadMoreAble.OnLoadMoreListener {
            override fun onLoadMore() {
                shortPageView.startLoadingMore()
                viewModel.getRecommend(true)
            }
        })
    }


    override fun onBackPressed(): Boolean {
        if (dataBind.commentLayout.isShowing()) {
            dataBind.commentLayout.hideComment()
            return true
        }
        return false
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        val player = dataBind.shortPage.controller().player() ?: return
        if (player.isInPlaybackState()) {
            if (!hidden) {
                player.pause()
            }else {
                player.start()
            }
        }
    }
}