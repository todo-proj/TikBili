package com.benyq.tikbili.scene.shortvideo.ui

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ui.DataState
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.databinding.FragmentShortVideoBinding
import com.benyq.tikbili.player.dispather.Event
import com.benyq.tikbili.player.dispather.EventDispatcher
import com.benyq.tikbili.scene.HorizontalVideoActivity
import com.benyq.tikbili.scene.HorizontalVideoContract
import com.benyq.tikbili.scene.SceneEvent
import com.benyq.tikbili.scene.shortvideo.event.ActionCommentVisible
import com.benyq.tikbili.scene.shortvideo.ui.comment.ShortVideoCommentView
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
    private val launcher = registerForActivityResult(HorizontalVideoContract()) {

    }

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        val shortPageView = dataBind.shortPage
        shortPageView.setLifeCycle(lifecycle)
        lifecycleScope.launch {
            viewModel.videoEvent.collect {
                when (it) {
                    is DataState.Loading -> {
                        dataBind.loading.isVisible = it.loading
                    }

                    is DataState.Success -> {
                        shortPageView.setItems(it.data)
                    }

                    is DataState.Error -> {}
                }
            }
        }
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
                        dataBind.commentLayout.submitComment(it.data)
                    }

                    is DataState.Error -> {
                        L.e(this@ShortVideoFragment, "commentEvent collect", "error", it.error?.message)
                    }
                }
            }
        }

        dataBind.commentLayout.setOnCommentEventListener(object : ShortVideoCommentView.OnCommentEventListener {

            override fun loadMoreComments() {
            }

            override fun loadReplyComments(id: String) {

            }

            override fun changedState(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dataBind.shortPage.controller().dispatcher().obtain(ActionCommentVisible::class.java).init(true).dispatch()
                }
            }

            override fun changedOffset(bottomSheet: View, slideOffset: Float) {
                val fullHeight = dataBind.flContainer.height
                val offset = abs(slideOffset)
                val newHeight = fullHeight - (1 - offset) * bottomSheet.height
                dataBind.shortPage.updateLayoutParams {
                    height = newHeight.toInt()
                }
            }

        })
        viewModel.getRecommend()
        setupShortVideoPage()
    }

    private fun setupShortVideoPage() {
        dataBind.shortPage.controller().addPlaybackListener(object : EventDispatcher.EventListener {
            override fun onEvent(event: Event) {
                when (event.code) {
                    SceneEvent.Action.COMMENT -> {
                        L.d(this@ShortVideoFragment, "onEvent", event.code)
                        val currentItem = dataBind.shortPage.currentItem() ?: return
                        if (currentItem.id != currentVid) {
                            dataBind.commentLayout.resetComment()
                            currentVid = currentItem.id
                            viewModel.queryVideoReply(currentItem.id)
                        }
                        dataBind.shortPage.controller().dispatcher().obtain(ActionCommentVisible::class.java).init(false).dispatch()
                        dataBind.commentLayout.showComment()
                    }
                    SceneEvent.Action.THUMB_UP -> {}
                    SceneEvent.Action.FULLSCREEN -> {
                        val data = dataBind.shortPage.controller().player()?.getDataSource() ?: return
                        HorizontalVideoActivity.startActivity(requireActivity(), data)
                        requireActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    }
                }
            }
        })
    }

}