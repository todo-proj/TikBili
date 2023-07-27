package com.benyq.tikbili.ui.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import kotlin.math.min

/**
 *
 * @author benyq
 * @date 7/26/2023
 * 抖音loadingView
 */
class TiktokVideoLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "TiktokVideoLoadingView"
    }

    private var viewWidth = 0
    private var viewHeight = 0
    private var normalRadius = 0f
    private var contractRadius = 0f
    private var expandRadius = 0f

    private var outerRadius = 0f
    private var innerRadius = 0f

    private var isNormal = true
    private var circlePadding = 10


    private lateinit var blueCircleInfo: CircleInfo
    private lateinit var redCircleInfo: CircleInfo

    private var animSet: AnimatorSet? = null

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
    }

    init {
        //关闭硬件加速，不然 paint xfermode 总是出问题
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun startLoading() {
        animSet?.cancel()
        val circleSizeAnim = ObjectAnimator.ofFloat(0f, 1f, 0f)
        circleSizeAnim.addUpdateListener {
            val ratio = it.animatedValue as Float
            outerRadius = normalRadius + ratio * (expandRadius - normalRadius)
            innerRadius = normalRadius - ratio * (normalRadius - contractRadius)
            postInvalidate()
        }
        val positionAnim1 = ObjectAnimator.ofFloat(blueCircleInfo.startPoint.x, redCircleInfo.startPoint.x)
        positionAnim1.addUpdateListener {
            blueCircleInfo.currentPoint.x = it.animatedValue as Float
        }
        val positionAnim2 = ObjectAnimator.ofFloat(redCircleInfo.startPoint.x, blueCircleInfo.startPoint.x)
        positionAnim2.addUpdateListener {
            redCircleInfo.currentPoint.x = it.animatedValue as Float
        }
        circleSizeAnim.repeatMode = ValueAnimator.REVERSE
        circleSizeAnim.repeatCount = -1
        positionAnim1.repeatCount = -1
        positionAnim1.repeatMode = ValueAnimator.REVERSE
        positionAnim2.repeatCount = -1
        positionAnim2.repeatMode = ValueAnimator.REVERSE

        circleSizeAnim.addListener(onRepeat = {
            isNormal = !isNormal
        })
        animSet = AnimatorSet().also {
            it.play(circleSizeAnim).with(positionAnim1).with(positionAnim2)
            it.interpolator = LinearInterpolator()
            it.duration = 600
            it.start()
        }
    }

    fun endLoading() {
        animSet?.cancel()
        animSet = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        circlePadding = viewWidth / 10

        normalRadius = (viewWidth - circlePadding) / 4f
        expandRadius = (viewWidth - circlePadding) / 3f
        contractRadius = (viewWidth - circlePadding) / 5f

        outerRadius = normalRadius
        innerRadius = normalRadius

        blueCircleInfo = CircleInfo(PointF(normalRadius, viewWidth / 2f), normalRadius, PointF(normalRadius, viewWidth / 2f))
        redCircleInfo = CircleInfo(PointF(viewWidth - normalRadius, viewWidth / 2f), normalRadius, PointF(viewWidth - normalRadius, viewWidth / 2f))

        startLoading()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (isNormal) {
                paint.color = Color.RED
                it.drawCircle(redCircleInfo.currentPoint.x, redCircleInfo.currentPoint.y, innerRadius, paint)
                paint.color = Color.BLUE
                it.drawCircle(blueCircleInfo.currentPoint.x, blueCircleInfo.currentPoint.y, outerRadius, paint)
            }else {
                paint.color = Color.BLUE
                it.drawCircle(blueCircleInfo.currentPoint.x, blueCircleInfo.currentPoint.y, innerRadius, paint)
                paint.color = Color.RED
                it.drawCircle(redCircleInfo.currentPoint.x, redCircleInfo.currentPoint.y, outerRadius, paint)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        endLoading()
        Log.d(TAG, "onDetachedFromWindow: endLoading")
    }

    data class CircleInfo(val startPoint: PointF, var startRadius: Float, val currentPoint: PointF)
}