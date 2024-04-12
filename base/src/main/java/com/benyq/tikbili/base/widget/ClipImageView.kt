package com.benyq.tikbili.base.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.use
import com.benyq.tikbili.base.R
import com.benyq.tikbili.base.ext.px

/**
 * Created on 2021/7/23 0023 19:14.


 * 可对图片进行圆角矩形、圆形显示的 ImageView
 */
class ClipImageView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val clipPath = Path()
    var type = Type.Circle
    var radius = 8f.px
        private set(value) {
            field = value
            invalidate()
        }
    var boundColor: Int = Color.WHITE
        private set(value) {
            field = value
            paint.color = value
            invalidate()
        }
    private var enableBound: Boolean = false

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.strokeWidth = w / 15f
    }

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.ClipImageView)
        }?.use {
            val typeString = it.getString(R.styleable.ClipImageView_clip_type)?.lowercase()
            when (typeString) {
                "circle" -> type = Type.Circle
                "roundrect" -> type = Type.RoundRect
                "none" -> type = Type.None
            }
            val radiusValue = it.getFloat(R.styleable.ClipImageView_round_radius, radius)
            boundColor = it.getColor(R.styleable.ClipImageView_bound_color, boundColor)
            enableBound = it.getBoolean(R.styleable.ClipImageView_enable_bound, enableBound)
            radius = radiusValue
        }

    }

    override fun onDraw(canvas: Canvas) {
        clipPath.reset()
        val width = width
        val height = height
        val min = Math.min(width, height)
        when (type) {
            Type.Circle -> {
                clipPath.addCircle(width / 2f, height / 2f, min / 2f, Path.Direction.CW)
            }

            Type.RoundRect -> {
                clipPath.addRoundRect(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    radius,
                    radius,
                    Path.Direction.CW
                )
            }

            Type.None -> {}
        }
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        when (type) {
            Type.Circle -> {
                canvas.drawCircle(width / 2f, height / 2f, min / 2f - 1, paint)
            }

            Type.RoundRect -> {
                canvas.drawRoundRect(
                    1f,
                    1f,
                    width.toFloat() - 1f,
                    height.toFloat() - 1f,
                    radius,
                    radius,
                    paint
                )
            }

            Type.None -> {}
        }
    }

    enum class Type {
        Circle, RoundRect, None
    }
}