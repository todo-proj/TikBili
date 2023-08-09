package com.benyq.tikbili.ui.test

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentTestBinding
import com.benyq.tikbili.ext.fullScreen
import com.benyq.tikbili.ui.base.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder

/**
 *
 * @author benyq
 * @date 7/26/2023
 *
 */
class TestFragment: BaseFragment<FragmentTestBinding>(R.layout.fragment_test) {

    private var fullScreen = false
    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        dataBind.rvComments.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = object: BaseQuickAdapter<String, QuickViewHolder>() {
            override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {
                holder.getView<TextView>(R.id.tv_comment).text = item ?: ""
            }
            override fun onCreateViewHolder(
                context: Context,
                parent: ViewGroup,
                viewType: Int,
            ): QuickViewHolder {
                return QuickViewHolder(R.layout.item_video_comment, parent)
            }
        }
        dataBind.rvComments.adapter = adapter
        adapter.submitList((1..50).toList().map { "num: $it" })

        dataBind.ivReply.setOnClickListener {
            dataBind.refreshView.open()
        }
        dataBind.flVideo.setOnClickListener {
            if (dataBind.refreshView.canCloseComment()) {
                dataBind.refreshView.close()
            }
        }
        dataBind.ivFullscreen.setOnClickListener {
            fullScreen = !fullScreen
            requireActivity().requestedOrientation =
                if (fullScreen) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            fullScreen(fullScreen)
        }
    }
}