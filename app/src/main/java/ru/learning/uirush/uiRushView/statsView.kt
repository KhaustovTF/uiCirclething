package ru.learning.lastrushcoding.uib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.learning.uirush.R
import ru.learning.uirush.utils.AndroidUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var textSizePx = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidthPx = AndroidUtils.dp(context, 16).toFloat()

    private var colors: IntArray = intArrayOf(
        0xFFFF2D55.toInt(),
        0xFF5856D6.toInt(),
        0xFF34C759.toInt(),
        0xFFFFCC00.toInt(),
    )

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.BUTT
        strokeWidth = lineWidthPx
    }

    private val capPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        textSize = textSizePx
    }

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSizePx = getDimension(R.styleable.StatsView_textSize, textSizePx)
            lineWidthPx = getDimension(R.styleable.StatsView_lineWidth, lineWidthPx)

            val c1 = getColor(R.styleable.StatsView_color1, colors[0])
            val c2 = getColor(R.styleable.StatsView_color2, colors[1])
            val c3 = getColor(R.styleable.StatsView_color3, colors[2])
            val c4 = getColor(R.styleable.StatsView_color4, colors[3])
            colors = intArrayOf(c1, c2, c3, c4)
        }

        arcPaint.strokeWidth = lineWidthPx
        textPaint.textSize = textSizePx

        requestLayout()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidthPx
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return

        val sum = data.sum()
        if (sum <= 0F) return

        val sweeps = data.map { v ->
            if (v <= 0F) 0F else (v / sum) * 360F
        }

        val totalSweep = sweeps.sum()
        val closesCircle = abs(totalSweep - 360F) < 0.5F

        var startAngle = -90F
        sweeps.forEachIndexed { index, sweep ->
            if (sweep <= 0F) return@forEachIndexed
            setColor(index)
            canvas.drawArc(oval, startAngle, sweep, false, arcPaint)
            startAngle += sweep
        }

        startAngle = -90F
        sweeps.forEachIndexed { index, sweep ->
            if (sweep <= 0F) return@forEachIndexed
            setColor(index)
            drawCap(canvas, startAngle)
            startAngle += sweep
        }

        if (!closesCircle) {
            val lastIndexWithValue = sweeps.indexOfLast { it > 0F }
            if (lastIndexWithValue != -1) {
                val endAngle = -90F + sweeps.take(lastIndexWithValue + 1).sum()
                setColor(lastIndexWithValue)
                drawCap(canvas, endAngle)
            }
        }

        canvas.drawText(
            "100.00%",
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun setColor(index: Int) {
        val c = colors[index % colors.size]
        arcPaint.color = c
        capPaint.color = c
    }

    private fun drawCap(canvas: Canvas, angleDeg: Float) {
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val x = (center.x + cos(angleRad) * radius).toFloat()
        val y = (center.y + sin(angleRad) * radius).toFloat()
        canvas.drawCircle(x, y, lineWidthPx / 2F, capPaint)
    }
}