package com.benyq.tikbili.ui.video

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.benyq.tikbili.bilibili.model.RecommendVideoModel

class VideoStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val dataList = mutableListOf<RecommendVideoModel>()

    override fun getItemCount() = dataList.size
    override fun createFragment(position: Int): Fragment {
        return FragmentVideoPlay.newInstance(dataList[position])
    }

    fun updateData(data: List<RecommendVideoModel>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

}