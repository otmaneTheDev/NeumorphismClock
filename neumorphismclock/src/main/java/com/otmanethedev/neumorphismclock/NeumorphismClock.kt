package com.otmanethedev.neumorphismclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class NeumorphismClock(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attributeSet, defStyleAttr) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    // Variable declaration
    private var mHeight: Int = 0

    private var mWidth: Int = 0
    private val mClockHours = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    // Radius of the clock
    private var mRadius: Int = 0

    // Paint object to draw in the canvas
    private var mPaint: Paint = Paint()

    // Flag to check if we initialized variables
    private var isInitialized: Boolean = false

    // Declare some size variables
    private var iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, context.resources.displayMetrics)
    private var defaultMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics)
    private var textFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, resources.displayMetrics)
    private val handSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics)

    // Custom font
    private var montserratTypeFace = ResourcesCompat.getFont(context, R.font.montserrat)

    // Drawable
    private var dayPeriodIcon = ContextCompat.getDrawable(context, R.drawable.ic_sun)

    // Declare colors
    private var lightBackgroundColor = ContextCompat.getColor(context, R.color.defaultLightBackgroundColor)
    private var lightShadowColor = ContextCompat.getColor(context, R.color.defaultLightShadowColor)
    private var darkShadowColor = ContextCompat.getColor(context, R.color.defaultDarkShadowColor)
    private var borderColor = ContextCompat.getColor(context, R.color.defaultBorderColor)
    private var minHourHandsColor = ContextCompat.getColor(context, R.color.defaultMinHourHandsColor)
    private var secondsHandColor = ContextCompat.getColor(context, R.color.defaultSecondsHandsColor)
    private var textColor = ContextCompat.getColor(context, R.color.defaultTextColor)
    private var iconColor = ContextCompat.getColor(context, R.color.defaultIconColor)

    private val dateFormatter = SimpleDateFormat("EE dd")
    private val calendar = Calendar.getInstance()
    private var currentDay = ""

    // RectF for hour text
    private val mRect = Rect()

    init {
        setUpAttributes(attributeSet)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) initialize()

        // We will draw an outer shadow in the circle so we scale the canvas
        canvas.scale(0.9f, 0.9f, (mWidth / 2).toFloat(), (mHeight / 2).toFloat())

        // Draw de clock shape
        drawClockShape(canvas)

        // Draw clock icon
        drawClockPeriod(canvas)

        // Draw current day text
        drawCurrentDayText(canvas)

        // Draw hours numerals
        drawNumerals(canvas)

        // Draw hands
        drawHands(canvas)

        // Draw center circle
        drawCenterCircle(canvas)

    }

    // Public methods

    fun setCurrentDay(value: String) {
        currentDay = value
    }

    fun setDayPeriodIcon(value: Int) {
        dayPeriodIcon = ContextCompat.getDrawable(context, value)
    }

    fun setLightBackgroundColor(value: Int) {
        lightBackgroundColor = ContextCompat.getColor(context, value)
    }

    fun setLightShadowColor(value: Int) {
        lightShadowColor = ContextCompat.getColor(context, value)
    }

    fun setDarkShadowColor(value: Int) {
        darkShadowColor = ContextCompat.getColor(context, value)
    }

    fun setBorderColor(value: Int) {
        borderColor = ContextCompat.getColor(context, value)
    }

    fun setMinHourHandsColor(value: Int) {
        minHourHandsColor = ContextCompat.getColor(context, value)
    }

    fun setSecondsHandColor(value: Int) {
        secondsHandColor = ContextCompat.getColor(context, value)
    }

    fun setTextColor(value: Int) {
        textColor = ContextCompat.getColor(context, value)
    }

    fun setIconColor(value: Int) {
        iconColor = ContextCompat.getColor(context, value)
    }

    // Private methods

    private fun setUpAttributes(attributes: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attributes, R.styleable.NeumorphismClock, 0, 0)

        lightBackgroundColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_lightBackgroundColor, R.color.defaultLightBackgroundColor))
        lightShadowColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_lightShadowColor, R.color.defaultLightShadowColor))
        darkShadowColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_darkShadowColor, R.color.defaultDarkShadowColor))
        borderColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_borderColor, R.color.defaultBorderColor))
        minHourHandsColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_minHourHandsColor, R.color.defaultMinHourHandsColor))
        secondsHandColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_secondsHandColor, R.color.defaultSecondsHandsColor))
        textColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_textColor, R.color.defaultTextColor))
        textFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(R.styleable.NeumorphismClock_textSize, textFontSize), context.resources.displayMetrics)
        iconColor = ContextCompat.getColor(context, typedArray.getResourceId(R.styleable.NeumorphismClock_iconColor, R.color.defaultIconColor))
        iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(R.styleable.NeumorphismClock_iconSize, iconSize), context.resources.displayMetrics)

        typedArray.recycle()
    }

    private fun initialize() {
        mHeight = height
        mWidth = width

        val minHeightWidthValue = min(mHeight, mWidth)
        mRadius = (minHeightWidthValue / 2 - defaultMargin).toInt()

        currentDay = dateFormatter.format(calendar.time)
        mPaint.isAntiAlias = true

        isInitialized = true
    }

    private fun drawClockShape(canvas: Canvas) {
        // Draw bottom shadow
        mPaint.setShadowLayer(30f, 15f, 15f, darkShadowColor)
        canvas.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), (mRadius + 50).toFloat(), mPaint)

        // Draw top shadow
        mPaint.color = lightBackgroundColor
        mPaint.setShadowLayer(30f, -15f, -15f, lightShadowColor)
        canvas.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), (mRadius + 50).toFloat(), mPaint)

        // Draw border
        mPaint.strokeWidth = 4f
        mPaint.style = Paint.Style.STROKE
        mPaint.color = borderColor
        canvas.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), (mRadius + 50).toFloat(), mPaint)

        mPaint.reset()
    }

    private fun drawClockPeriod(canvas: Canvas) {
        val xPosition = (mWidth / 2).toFloat()
        val yPosition = ((mHeight / 2) - (mRadius / 1.5)).toFloat()

        // Draw top shadow
        mPaint.setShadowLayer(30f, 30f, 30f, darkShadowColor)
        canvas.drawCircle(xPosition, yPosition, iconSize / 2, mPaint)

        // Draw bottom shadow
        mPaint.color = lightBackgroundColor
        mPaint.setShadowLayer(30f, -30f, -30f, lightShadowColor)
        canvas.drawCircle(xPosition, yPosition, (iconSize + defaultMargin) / 2, mPaint)

        // Draw border
        mPaint.strokeWidth = 4f
        mPaint.style = Paint.Style.STROKE
        mPaint.color = borderColor
        canvas.drawCircle(xPosition, yPosition, (iconSize + defaultMargin) / 2, mPaint)

        // Add center icon
        dayPeriodIcon?.setTint(iconColor)
        dayPeriodIcon?.setBounds(
            (xPosition - (iconSize / 2) + defaultMargin / 2).toInt(),
            (yPosition - (iconSize / 2) + defaultMargin / 2).toInt(),
            (xPosition + (iconSize / 2) - defaultMargin / 2).toInt(),
            (yPosition + (iconSize / 2) - defaultMargin / 2).toInt()
        )

        dayPeriodIcon?.draw(canvas)

        mPaint.reset()
    }

    private fun drawCurrentDayText(canvas: Canvas) {
        val xPosition = (mWidth / 2).toFloat()
        val yPosition = ((mHeight / 2) + (mRadius / 1.3)).toFloat()

        mPaint.textSize = textFontSize
        mPaint.textAlign = Paint.Align.CENTER

        val textSize = mPaint.measureText(currentDay) / 2
        val rectF = RectF(xPosition - textSize - defaultMargin, yPosition - textSize, xPosition + textSize + defaultMargin, yPosition + defaultMargin)
        mPaint.getTextBounds(currentDay, 0, currentDay.length, mRect)

        // Draw top shadow
        mPaint.setShadowLayer(30f, 30f, 30f, darkShadowColor)
        canvas.drawRoundRect(rectF, defaultMargin, defaultMargin, mPaint)

        // Draw bottom shadow
        mPaint.color = lightBackgroundColor
        mPaint.setShadowLayer(30f, -30f, -30f, lightShadowColor)
        canvas.drawRoundRect(rectF, defaultMargin, defaultMargin, mPaint)

        // Draw border
        mPaint.strokeWidth = 2f
        mPaint.style = Paint.Style.STROKE
        mPaint.color = borderColor
        canvas.drawRoundRect(rectF, defaultMargin, defaultMargin, mPaint)

        // Draw current day text
        mPaint.typeface = montserratTypeFace
        mPaint.style = Paint.Style.FILL
        mPaint.isFakeBoldText = true
        mPaint.color = textColor
        canvas.drawText(currentDay, rectF.centerX(), (rectF.centerY() - defaultMargin) + (textSize / 2), mPaint)

        mPaint.reset()
    }

    private fun drawNumerals(canvas: Canvas) {
        mPaint.textSize = textFontSize
        mPaint.isFakeBoldText = true
        mPaint.color = textColor

        for (hour in mClockHours) {
            var tmp = hour.toString()

            mPaint.getTextBounds(tmp, 0, tmp.length, mRect)
            val angle = Math.PI / 6 * (hour - 3)
            val x = (mWidth / 2 + cos(angle) * mRadius - mRect.width() / 2).toFloat()
            val y = ((mHeight / 2).toDouble() + sin(angle) * mRadius + (mRect.height() / 2)).toFloat()

            if (listOf(12, 3, 6, 9).contains(hour)) {
                canvas.drawText(tmp, x, y, mPaint)
            } else {
                canvas.drawText("·", x, y, mPaint)
            }
        }
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)

        drawHandLine(canvas, (hour + calendar.get(Calendar.MINUTE) / 60f) * 5f, HandType.HOUR)
        drawHandLine(canvas, calendar.get(Calendar.MINUTE).toFloat(), HandType.MINUTE)
        drawHandLine(canvas, calendar.get(Calendar.SECOND).toFloat(), HandType.SECONDS)

        postInvalidateDelayed(500)
        invalidate()

        mPaint.reset()
    }

    private fun drawHandLine(canvas: Canvas, value: Float, handType: HandType) {
        val angle = Math.PI * value / 30 - Math.PI / 2

        val handRadius = when (handType) {
            HandType.HOUR -> mRadius - mRadius / 3
            HandType.MINUTE -> mRadius - mRadius / 6
            HandType.SECONDS -> mRadius - mRadius / 9
        }

        mPaint.color = if (handType == HandType.SECONDS) secondsHandColor else minHourHandsColor
        mPaint.strokeWidth = if (handType == HandType.SECONDS) handSize else handSize * 2
        mPaint.strokeCap = Paint.Cap.ROUND

        canvas.drawLine(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mWidth / 2 + cos(angle) * handRadius).toFloat(),
            (mHeight / 2 + sin(angle) * handRadius).toFloat(),
            mPaint
        )
    }

    private fun drawCenterCircle(canvas: Canvas) {
        mPaint.color = secondsHandColor
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, handSize, mPaint)
    }

    private enum class HandType { HOUR, MINUTE, SECONDS }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}
