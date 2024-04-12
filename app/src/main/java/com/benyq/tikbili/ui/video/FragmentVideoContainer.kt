package com.benyq.tikbili.ui.video

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentVideoContainerBinding
import com.benyq.tikbili.base.ext.gone
import com.benyq.tikbili.base.ext.overScrollNever
import com.benyq.tikbili.base.ext.visible
import com.benyq.tikbili.ui.base.BaseFragment
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.base.mvi.extension.collectState

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
class FragmentVideoContainer: BaseFragment<FragmentVideoContainerBinding>(R.layout.fragment_video_container) {

    private val viewModel by viewModels<VideoContainerViewModel>()
    private val fragmentAdapter by lazy { VideoStateAdapter(this) }

    override fun onFragmentCreated(savedInstanceState: Bundle?) {
        dataBind.vpVideo.offscreenPageLimit = 1
        dataBind.vpVideo.adapter = fragmentAdapter
        dataBind.vpVideo.orientation = ViewPager2.ORIENTATION_VERTICAL
        dataBind.vpVideo.overScrollNever()
        dataBind.vpVideo.registerOnPageChangeCallback(object: OnPageChangeCallback() {
        })
        dataBind.refreshView.setOnLoadingListener {
            //刷新
            viewModel.loadHomeVideo(true)
        }
        viewModel.container.singleEventFlow.collectSingleEvent(viewLifecycleOwner) {
            when(it) {
                is VideoContainerEvent.ToastEvent -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
                is VideoContainerEvent.VideoModelEvent -> {
                    if (it.loadMore) {
                        fragmentAdapter.addAll(it.data)
                        dataBind.refreshView.finishLoading()
                    }else {
                        fragmentAdapter.submit(it.data)
                    }
                }
            }
        }
        viewModel.container.uiStateFlow.collectState(viewLifecycleOwner) {
            collectPartial(VideoContainerState::loading) {
                if (it) {
                    dataBind.flLoading.visible()
                }else {
                    dataBind.flLoading.gone()
                }
            }
        }
        viewModel.loadHomeVideo()
    }
}