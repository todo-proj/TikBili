package com.benyq.tikbili.scene.shortvideo.ui.comment


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.tikbili.base.ext.px
import com.benyq.tikbili.base.utils.L
import com.benyq.tikbili.databinding.ViewShortVideoCommentBinding
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 *
 * @author benyq
 * @date 4/17/2024
 * 短视频评论区
 */
class ShortVideoCommentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewShortVideoCommentBinding
    private var _commentListener: OnCommentEventListener? = null
    private val adapter by lazy { ShortVideoCommentAdapter {

    } }
    private lateinit var adapterHelper: QuickAdapterHelper
    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    fun setOnCommentEventListener(commentClickListener: OnCommentEventListener) {
        _commentListener = commentClickListener
    }

    init {
        binding = ViewShortVideoCommentBinding.inflate(LayoutInflater.from(context), this, true)
        binding.cvClose.setOnClickListener {
            hideComment()
        }

        binding.rvComments.layoutManager = LinearLayoutManager(context)
        adapterHelper = QuickAdapterHelper.Builder(adapter)
            .setTrailingLoadStateAdapter(object: TrailingLoadStateAdapter.OnTrailingListener {
                override fun onFailRetry() {
                    _commentListener?.loadMoreComments()
                }
                override fun onLoad() {
                    _commentListener?.loadMoreComments()
                }

                override fun isAllowLoading(): Boolean {
                    return super.isAllowLoading()
                }
            })
            .build()
        binding.rvComments.adapter = adapterHelper.adapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.clBottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                L.d(this@ShortVideoCommentView, "onStateChanged", newState)
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    isVisible = false
                }
                _commentListener?.changedState(bottomSheet, newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                L.d(this@ShortVideoCommentView, "onSlide", slideOffset)
                _commentListener?.changedOffset(bottomSheet, slideOffset)
            }
        })
        bottomSheetBehavior.isHideable = true
//        bottomSheetBehavior.peekHeight = 500.px
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.coordinator.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun submitComment(data: List<CommentModel>) {
        adapter.submitList(data)
        adapterHelper.trailingLoadState = LoadState.NotLoading(false)
    }

    fun appendComment(data: List<CommentModel>, isEnd: Boolean) {
        adapter.addAll(data)
        adapterHelper.trailingLoadState = LoadState.NotLoading(isEnd)
    }

    fun failLoad(throwable: Throwable?) {
        if (throwable == null) return
        if (adapter.itemCount == 0) {
            //TODO 显示 error message
        }else {
            adapterHelper.trailingLoadState = LoadState.Error(throwable)
        }
    }

    fun addReplyComment(id: String, data: List<CommentModel>) {

    }

    fun showLoading() {
        if (adapter.itemCount == 0) {
            binding.loading.isVisible = true
        }else {
            adapterHelper.trailingLoadState = LoadState.Loading
        }
    }

    fun dismissLoading() {
        if (binding.loading.isVisible) {
            binding.loading.endLoading()
            binding.loading.isVisible = false
        }
    }

    fun showComment() {
        if (!isVisible) {
            isVisible = true
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun isShowing(): Boolean {
        return isVisible || bottomSheetBehavior.state <= BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * 重置评论页面，移除评论
     */
    fun resetComment() {
        adapter.submitList(emptyList())
        adapterHelper.trailingLoadState = LoadState.None
    }

    fun hideComment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }


    override fun canScrollVertically(direction: Int): Boolean {
        return binding.rvComments.canScrollVertically(direction)
    }

    interface OnCommentEventListener {
        fun loadMoreComments()
        fun loadReplyComments(id: String)

        fun changedState(bottomSheet: View, newState: Int)
        fun changedOffset(bottomSheet: View,slideOffset: Float)
    }

}