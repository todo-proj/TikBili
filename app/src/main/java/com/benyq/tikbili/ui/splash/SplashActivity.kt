package com.benyq.tikbili.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.benyq.tikbili.R
import com.benyq.tikbili.databinding.ActivitySplashBinding
import com.benyq.tikbili.ui.base.BaseActivity
import com.benyq.tikbili.ui.base.mvi.extension.collectSingleEvent
import com.benyq.tikbili.ui.login.LoginActivity
import com.benyq.tikbili.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val viewModel by viewModels<SplashViewModel>()

    override fun getLayoutId() = R.layout.activity_splash

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.container.singleEventFlow.collectSingleEvent(this) {
            when(it) {
                is SplashEvent.ToMainEvent -> {
                    goActivity(Intent(this, MainActivity::class.java))
                }
                is SplashEvent.ToLoginEvent -> {
                    goActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }
        viewModel.checkLogin()
    }

    private fun goActivity(intent: Intent) {
        startActivity(intent)
        finish()
    }
}