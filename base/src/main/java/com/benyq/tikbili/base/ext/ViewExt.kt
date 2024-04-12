package com.benyq.tikbili.base.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.throttleClick(
    interval: Long = 500,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    block: View.() -> Unit
) {
    setOnClickListener(ThrottleClickListener(interval, unit, block))
}

class ThrottleClickListener(
    private val interval: Long = 500,
    private val unit: TimeUnit = TimeUnit.MILLISECONDS,
    private var block: View.() -> Unit
) : View.OnClickListener {
    private var lastTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > unit.toMillis(interval)) {
            lastTime = currentTime
            block(v)
        }
    }
}

fun ViewPager2.overScrollNever() {
    val child: View = getChildAt(0)
    (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
}

fun ViewPager2.isSlideToBottom(): Boolean {
    return (getChildAt(0) as? RecyclerView)?.isSlideToBottom() ?: false
}

fun RecyclerView.isSlideToBottom(): Boolean {
    return computeVerticalScrollExtent() + computeVerticalScrollOffset() >= computeVerticalScrollRange()
}

/**
 * 可以在一个屏幕上显示多项item
 */
fun ViewPager2.multiscreenDisplay(left: Int, top: Int, right: Int, bottom: Int) {
    (getChildAt(0) as? RecyclerView)?.let {
        it.setPadding(left, top, right, bottom)
        it.clipToPadding = false
    }
}

fun ViewPager2.findItemViewByPosition(position: Int): View? {
    return (getChildAt(0) as? RecyclerView)?.layoutManager?.findViewByPosition(position)
}

/**
 * 配合 flow sample操作符，可以实现防止快速点击效果
 */
fun View.clickFlow() = callbackFlow {
    setOnClickListener {
        trySend(this@clickFlow)
    }
    awaitClose {
        setOnClickListener(null)
    }
}

/**
 * 配合 flow debounce 操作符
 * 可以实现搜索框在一定时间内，不会每一个字符输入都查询的功能
 */
fun EditText.textFlow() = callbackFlow {
    val watcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let { trySend(it) }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
    addTextChangedListener(watcher)
    awaitClose { removeTextChangedListener(watcher) }
}