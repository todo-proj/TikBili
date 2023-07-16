package com.benyq.tikbili.ui.video

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.FragmentVideoContainerBinding
import com.benyq.tikbili.ext.hideLoading
import com.benyq.tikbili.ext.overScrollNever
import com.benyq.tikbili.ext.showLoading
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
        dataBind.vpVideo.adapter = fragmentAdapter
        dataBind.vpVideo.orientation = ViewPager2.ORIENTATION_VERTICAL
        dataBind.vpVideo.overScrollNever()

        viewModel.homePageContainer.singleEventFlow.collectSingleEvent(viewLifecycleOwner) {
            when(it) {
                is VideoContainerEvent.ToastEvent -> {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.homePageContainer.uiStateFlow.collectState(viewLifecycleOwner) {
            collectPartial(VideoContainerState::loading) {
                if (it) {
                    requireActivity().showLoading()
                }else {
                    requireActivity().hideLoading()
                }
            }
            collectPartial(VideoContainerState::data) {
                fragmentAdapter.updateData(it)
            }
        }
        viewModel.loadHomeVideo()
    }
}