package com.benyq.tikbili.ui.main

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.benyq.tikbili.R
import com.benyq.tikbili.base.ext.isAppearanceLightStatusBars
import com.benyq.tikbili.base.ext.systemBarColor
import com.benyq.tikbili.databinding.ActivityMainBinding
import com.benyq.tikbili.scene.shortvideo.ui.ShortVideoFragment
import com.benyq.tikbili.ui.base.BaseActivity
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.base.mvi.extension.collectState
import com.benyq.tikbili.ui.dynamic.DynamicFragment
import com.benyq.tikbili.ui.mine.MineFragment


class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val KEY_DYNAMIC = "dynamic"
        const val KEY_VIDEO = "shortVideo"
        const val KEY_MINE = "mine"
    }

    private val viewModel by viewModels<MainViewModel>()
    override fun getLayoutId() = R.layout.activity_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        viewModel.mainContainer.uiStateFlow.collectState(this) {

        }
        viewModel.mainContainer.singleEventFlow.collectSingleEvent(this) {
        }
        setupBottomNavigation()
    }

    private fun showFragment(tag: String) {
        supportFragmentManager.beginTransaction().let {
            val currentFragment =
                supportFragmentManager.findFragmentByTag(viewModel.currentFragmentTag)
            currentFragment?.let { fragment -> it.hide(fragment) }
            val showFragment: Fragment? =
                supportFragmentManager.findFragmentByTag(tag) ?: when (tag) {
                    KEY_VIDEO -> {
                        ShortVideoFragment().apply {
                            it.add(R.id.fl_container, this, tag)
                        }
                    }
                    KEY_MINE -> {
                        MineFragment().apply {
                            it.add(R.id.fl_container, this, tag)
                        }
                    }
                    KEY_DYNAMIC -> {
                        DynamicFragment().apply {
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

    private fun setupBottomNavigation() {
        dataBind.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_navigation_dynamic -> {
                    isAppearanceLightStatusBars(true)
                    showFragment(KEY_DYNAMIC)
                }
                R.id.home_navigation_video -> {
                    isAppearanceLightStatusBars(false)
                    showFragment(KEY_VIDEO)
                }
                R.id.home_navigation_mine -> {
                    isAppearanceLightStatusBars(true)
                    showFragment(KEY_MINE)
                }
            }
            true
        }
        dataBind.bottomNavigation.selectedItemId = when (viewModel.currentFragmentTag) {
            KEY_VIDEO -> R.id.home_navigation_video
            KEY_MINE -> R.id.home_navigation_mine
            KEY_DYNAMIC -> R.id.home_navigation_dynamic
            else -> R.id.home_navigation_video
        }
    }
}