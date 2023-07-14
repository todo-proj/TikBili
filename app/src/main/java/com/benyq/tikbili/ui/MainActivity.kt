package com.benyq.tikbili.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.ActivityMainBinding
import com.benyq.tikbili.ui.base.BaseActivity
import com.benyq.tikbili.ui.video.FragmentVideoContainer


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()
    override fun getLayoutId() = R.layout.activity_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        showFragment("Video")
    }


    private fun showFragment(tag: String) {
        supportFragmentManager.beginTransaction().let {
            val currentFragment = supportFragmentManager.findFragmentByTag(viewModel.currentFragmentTag)
            currentFragment?.let { fragment -> it.hide(fragment) }
            val showFragment: Fragment? = supportFragmentManager.findFragmentByTag(tag) ?: when(tag) {
                "Video" -> {
                    FragmentVideoContainer().apply {
                        it.add(R.id.fl_container, this, tag)
                    }
                }
                else -> null
            }
            showFragment?.let { fragment ->
                it.show(fragment)
            }
            it.commit()
        }
        viewModel.currentFragmentTag = tag
    }
}