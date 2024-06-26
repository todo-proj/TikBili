package com.benyq.tikbili.ui.main

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.ActivityMainBinding
import com.benyq.tikbili.base.ext.systemBarColor
import com.benyq.tikbili.scene.shortvideo.ui.ShortVideoFragment
import com.benyq.tikbili.ui.base.BaseActivity
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.base.mvi.extension.collectState


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()
    override fun getLayoutId() = R.layout.activity_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        systemBarColor(Color.BLACK)
        showFragment("ShortVideo")
        viewModel.mainContainer.uiStateFlow.collectState(this) {

        }
        viewModel.mainContainer.singleEventFlow.collectSingleEvent(this) {
        }
    }


    private fun showFragment(tag: String) {
        supportFragmentManager.beginTransaction().let {
            val currentFragment =
                supportFragmentManager.findFragmentByTag(viewModel.currentFragmentTag)
            currentFragment?.let { fragment -> it.hide(fragment) }
            val showFragment: Fragment? =
                supportFragmentManager.findFragmentByTag(tag) ?: when (tag) {
                    "ShortVideo" -> {
                        ShortVideoFragment().apply {
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}