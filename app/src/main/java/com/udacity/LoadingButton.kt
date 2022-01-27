package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingTextWidth = 0
    private var defaultText: String
    private var loadingText: String

    private var butColor = 0
    private var fontColor = 0

    private val valueAnimator = ValueAnimator.ofInt(0, 100)
    private val circleAnimation = ValueAnimator.ofFloat(0F, 360F)

    private val animationRect = RectF()
    private val animationCrycleRect = RectF()
    private val animationPath = Path()
    private var animationAngle = 0F


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed)
    { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        textSize = 55.0f
    }
    private val animationPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    private val kreisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private val pointCenterPosition: PointF = PointF(0.0f, 0.0f)


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            butColor = getColor(R.styleable.LoadingButton_buttonColor, Color.BLACK)
            fontColor = getColor(R.styleable.LoadingButton_fontColor, Color.WHITE)
        }
        getTextWidth(context.getString(R.string.button_loading))
        defaultText = context.getString(R.string.button_defaulttext)
        loadingText = context.getString(R.string.button_loading)
        paint.color = fontColor
    }


    override fun onAnimationUpdate(animation: ValueAnimator?) {

        buttonState = ButtonState.Loading

        if (animation == valueAnimator) {
            val value: Int = animation?.animatedValue as Int
            val aniWidth: Float = (widthSize * value / 100).toFloat()

            animationRect.set(0F, 0F, aniWidth, heightSize.toFloat())
            animationPath.addRect(animationRect, Path.Direction.CW)
        } else {
            animationAngle = animation?.animatedValue as Float
        }

        invalidate()
    }


    private fun PointF.calculateCenter(widthVal: Int, heightVal: Int, textPaint: Paint) {
        x = (widthVal / 2).toFloat()
        y = (heightVal / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

    }

    private fun getTextWidth(theText: String) {
        val rect = Rect()
        paint.getTextBounds(theText, 0, theText.length, rect)
        loadingTextWidth = rect.width()
    }

    fun animateButton() {

        // Animation circle
        val xCircle = pointCenterPosition.x + loadingTextWidth / 2 + 10F
        val yCircle = heightSize / 2 - 25F
        animationCrycleRect.set(xCircle, yCircle, xCircle + 50F, yCircle + 50F)
        buttonState = ButtonState.Clicked

        valueAnimator.addUpdateListener(this)
        circleAnimation.addUpdateListener(this)

        val set = AnimatorSet()
        set.playTogether(valueAnimator, circleAnimation)
        set.duration = 1000
        // When the animation is done, set buttonState
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                buttonState = ButtonState.Completed
            }
        })
        set.start()

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(butColor)

        if (buttonState == ButtonState.Loading) {
            canvas?.drawRect(animationRect, animationPaint)
            canvas?.drawText(loadingText, pointCenterPosition.x, pointCenterPosition.y, paint)
            canvas?.drawArc(animationCrycleRect, 0F, animationAngle, true, kreisPaint)
        } else {
            canvas?.drawText(defaultText, pointCenterPosition.x, pointCenterPosition.y, paint)
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        pointCenterPosition.calculateCenter(widthSize, heightSize, paint)
        setMeasuredDimension(w, h)
    }


}

