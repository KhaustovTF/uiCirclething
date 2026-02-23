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
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.BUTT
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )
        }

        paint.strokeWidth = lineWidth.toFloat()
        textPaint.textSize = textSize

        requestLayout()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
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

        val sweeps = data.map { datum ->
            if (datum <= 0F) 0F else (datum / sum) * 360F
        }
        val totalSweep = sweeps.sum()

        var startAngle = -90F

        // стартовый “кругляш” рисуем один раз
        paint.color = colorForIndex(0)
        drawCap(canvas, startAngle)

        sweeps.forEachIndexed { index, sweep ->
            if (sweep <= 0F) return@forEachIndexed

            paint.color = colorForIndex(index)
            canvas.drawArc(oval, startAngle, sweep, false, paint)

            val isLast = index == sweeps.lastIndex
            val closesCircle = abs(totalSweep - 360F) < 0.5F

            // чтобы не было двойного капа на стыке конца и начала при полном круге
            if (!(isLast && closesCircle)) {
                drawCap(canvas, startAngle + sweep)
            }

            startAngle += sweep
        }

        canvas.drawText(
            "%.2f%%".format(100F),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun drawCap(canvas: Canvas, angleDeg: Float) {
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val x = (center.x + cos(angleRad) * radius).toFloat()
        val y = (center.y + sin(angleRad) * radius).toFloat()
        canvas.drawCircle(x, y, lineWidth / 2F, paint)
    }

    private fun colorForIndex(index: Int): Int {
        if (colors.isEmpty()) return generateRandomColor()
        return colors[index % colors.size]
    }

    private fun generateRandomColor(): Int {
        return Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
    }
}