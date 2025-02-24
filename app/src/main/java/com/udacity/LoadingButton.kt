package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator : ValueAnimator

    private var loadingProgress = 0.0

    private var btnColor = 0
    private var btnColorLoading = 0
    private var textColor = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        loadingProgress = (it.animatedValue as Float).toDouble()
        invalidate()
    }


    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(context, R.animator.loading_animator) as ValueAnimator
        valueAnimator.addUpdateListener(updateListener)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            btnColor = getColor(R.styleable.LoadingButton_btn_color, 0)
            btnColorLoading = getColor(R.styleable.LoadingButton_btn_color_loading, 0)
            textColor = getColor(R.styleable.LoadingButton_text_color, 0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw initial rectangle for the button
        paint.color = btnColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), height.toFloat(), paint)

        // Draw loading progress rectangle
        if (buttonState == ButtonState.Loading) {
            paint.color = btnColorLoading
            canvas?.drawRect(0f, 0f, (widthSize * loadingProgress/100).toFloat(), height.toFloat(), paint)
        }

        // Set button text
        val btnText : String
        if (buttonState == ButtonState.Loading) {
            btnText = resources.getString(R.string.button_loading)
        } else {
            btnText = resources.getString(R.string.button_name)
        }
        paint.color = textColor
        canvas?.drawText(btnText, (widthSize/2).toFloat(), (heightSize/2 + 15).toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Loading
            valueAnimator.start()
        }
        return super.performClick()
    }

}