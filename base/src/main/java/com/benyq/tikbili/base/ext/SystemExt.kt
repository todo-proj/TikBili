package com.benyq.tikbili.base.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun Activity.showLoading() {
    contentView()?.apply {
        val pb = ProgressBar(context)
        pb.tag = "pb"
        addView(pb, FrameLayout.LayoutParams(150, 150, Gravity.CENTER))
    }
}

fun Activity.hideLoading() {
    val pb = contentView()?.findViewWithTag<ProgressBar>("pb")
    pb?.let { contentView()?.removeView(it) }
}

fun Activity.contentView(): FrameLayout? {
    return takeIf { !isFinishing && !isDestroyed }?.window?.decorView?.findViewById(android.R.id.content)
}

fun Activity.systemBarColor(@ColorInt color: Int) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    window.statusBarColor = color
    window.navigationBarColor = color
    val luminance = ColorUtils.calculateLuminance(color)
    insetsController.isAppearanceLightStatusBars = luminance > 0.5
}

fun Activity.fullScreen(fullScreen: Boolean = true) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (fullScreen) {
        setDecorFitsSystemWindows(window, false)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        //允许window 的内容可以上移到刘海屏状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }else {
        setDecorFitsSystemWindows(window, true)
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
    }

}

fun Fragment.systemBarColor(@ColorInt color: Int) {
    requireActivity().systemBarColor(color)
}

fun Fragment.fullScreen(fullScreen: Boolean = true) {
    requireActivity().fullScreen(fullScreen)
}


fun Context.getSystemBrightness(): Float {
    var systemBrightness = 0
    try {
        systemBrightness =
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
    }
    return systemBrightness / getSystemSettingBrightnessMax()
}

fun getSystemSettingBrightnessMax(): Float {
    try {
        val res = Resources.getSystem()
        val resId = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android")
        if (resId != 0) {
            return res.getInteger(resId).toFloat()
        }
    } catch (e: Exception) { /* ignore */
    }
    return 255f
}


suspend fun Context.alert(title: String, message: String): Boolean =
    suspendCancellableCoroutine { continuation ->
        AlertDialog.Builder(this)
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
                continuation.resume(false)
            }.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                continuation.resume(true)
            }.setTitle(title)
            .setMessage(message)
            .setOnCancelListener {
                continuation.resume(false)
            }.create()
            .also { dialog ->
                continuation.invokeOnCancellation {
                    dialog.dismiss()
                }
            }.show()
    }

fun fromTIRAMISU() = fromSpecificVersion(Build.VERSION_CODES.TIRAMISU)
fun beforeTIRAMISU() = beforeSpecificVersion(Build.VERSION_CODES.TIRAMISU)
fun fromSV2() = fromSpecificVersion(Build.VERSION_CODES.S_V2)
fun beforeSV2() = beforeSpecificVersion(Build.VERSION_CODES.S_V2)
fun fromS() = fromSpecificVersion(Build.VERSION_CODES.S)
fun beforeS() = beforeSpecificVersion(Build.VERSION_CODES.S)
fun fromR() = fromSpecificVersion(Build.VERSION_CODES.R)
fun beforeR() = beforeSpecificVersion(Build.VERSION_CODES.R)
fun fromQ() = fromSpecificVersion(Build.VERSION_CODES.Q)
fun beforeQ() = beforeSpecificVersion(Build.VERSION_CODES.Q)
fun fromM() = fromSpecificVersion(Build.VERSION_CODES.M)
fun beforeM() = beforeSpecificVersion(Build.VERSION_CODES.M)
fun fromN() = fromSpecificVersion(Build.VERSION_CODES.N)
fun beforeN() = beforeSpecificVersion(Build.VERSION_CODES.N)
fun fromO() = fromSpecificVersion(Build.VERSION_CODES.O)
fun beforeO() = beforeSpecificVersion(Build.VERSION_CODES.O)
fun fromP() = fromSpecificVersion(Build.VERSION_CODES.P)
fun beforeP() = beforeSpecificVersion(Build.VERSION_CODES.P)
fun fromSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT >= version
fun beforeSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT < version