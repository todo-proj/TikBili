package com.benyq.tikbili.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import com.benyq.tikbili.R
import com.benyq.tikbili.utils.StartLogHelper
import com.benyq.tikbili.databinding.ActivitySplashBinding
import com.benyq.tikbili.ext.systemBarColor
import com.benyq.tikbili.ui.base.BaseActivity
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.login.LoginActivity
import com.benyq.tikbili.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val viewModel by viewModels<SplashViewModel>()

    private var isMotionRunning = false

    override fun getLayoutId() = R.layout.activity_splash

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        systemBarColor(Color.BLACK)
        viewModel.container.singleEventFlow.collectSingleEvent(this) {
            when(it) {
                is SplashEvent.ToMainEvent -> {
                    waitLaunch(Intent(this, MainActivity::class.java))
                }
                is SplashEvent.ToLoginEvent -> {
                    waitLaunch(Intent(this, LoginActivity::class.java))
                }
            }
        }
        dataBind.guideHorizontal.post {
            dataBind.guideHorizontal.performClick()
        }
        dataBind.splashMotion.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
            ) {
                viewModel.checkLogin()
                isMotionRunning = true
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isMotionRunning = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
            ) {
            }

        })

        StartLogHelper.getMainTime()
    }

    private fun goActivity(intent: Intent) {
        startActivity(intent)
        finish()
    }

    private fun waitLaunch(intent: Intent) {
        lifecycleScope.launch {
            while (isMotionRunning) {
                delay(100)
            }
            goActivity(intent)
        }
    }
}