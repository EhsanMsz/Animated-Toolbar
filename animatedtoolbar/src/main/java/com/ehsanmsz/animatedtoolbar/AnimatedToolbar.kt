/*
 * Copyright 2020 Ehsan msz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ehsanmsz.animatedtoolbar

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.animation.BaseInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Px
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import com.ehsanmsz.animatedtoolbar.color.GradientColor
import com.ehsanmsz.animatedtoolbar.shape.Rect
import com.ehsanmsz.animatedtoolbar.shape.RoundedRect
import com.ehsanmsz.animatedtoolbar.shape.Shape
import kotlin.math.max

class AnimatedToolbar : Toolbar {

    companion object {
        private const val WIDTH_PROPERTY_NAME = "width"
        private const val HEIGHT_PROPERTY_NAME = "height"
        private const val VIEW_X_COEFFICIENT_PROPERTY_NAME = "view_width"
        private const val VIEW_Y_COEFFICIENT_PROPERTY_NAME = "view_height"
    }

    private var mWidth = 0
    private var mHeight = 0
    private var mColor: Int = 0

    @Px
    var toolbarHeight: Int = 0

    lateinit var gradientColor: GradientColor
    lateinit var shape: Shape
    var interpolator: Any = DecelerateInterpolator()
    var duration: Long = 400L //milliseconds

    private lateinit var paint: Paint
    private var displayMetrics: DisplayMetrics? = null
    private var path = Path()
    private var isApi21 = false

    var toolbarAnimationListener: ToolbarAnimationListener? = null
    private var viewAnimationListener: ViewAnimationUpdateListener? = null

    interface ToolbarAnimationListener {
        fun onAnimationStart()
        fun onAnimationEnd()
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.animatedToolbarStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyStyle(context, attrs, defStyleAttr)
        init(context)
    }

    private fun applyStyle(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val ta = context?.obtainStyledAttributes(
            attrs,
            R.styleable.AnimatedToolbar,
            defStyleAttr,
            R.style.AnimatedToolbar
        )

        ta?.apply {
            duration = getInteger(R.styleable.AnimatedToolbar_duration, 400).toLong()
            when (getInteger(R.styleable.AnimatedToolbar_shape, 0)) {
                0 -> {
                    shape = Rect(
                        getFloat(R.styleable.AnimatedToolbar_angle, 30f).toDouble(),
                        getInteger(R.styleable.AnimatedToolbar_shapeGravity, 0)
                    )
                }

                1 -> {
                    shape = RoundedRect()
                }
            }
            recycle()
        }
    }

    private fun init(context: Context?) {
        checkApiVersion()
        displayMetrics = context?.resources?.displayMetrics
        toolbarHeight = getDefaultActionBarSize()
        mColor = getPrimaryColor()
        paint = Paint().apply {
            isAntiAlias = true
            color = mColor
            style = Paint.Style.FILL
        }
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun checkApiVersion() {
        isApi21 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    @SuppressLint("newApi")
    private fun getPrimaryColor(): Int {
        val attr = if (isApi21) android.R.attr.colorPrimary else R.attr.colorPrimary
        return TypedValue().apply {
            context.theme.resolveAttribute(attr, this, true)
        }.data
    }

    @SuppressLint("newApi")
    private fun pathAnimation() {
        val widthPropertyValueHolder = PropertyValuesHolder.ofFloat(
            WIDTH_PROPERTY_NAME,
            shape.fromX * mWidth,
            shape.toX * mWidth
        )
        val heightPropertyValueHolder = PropertyValuesHolder.ofFloat(
            HEIGHT_PROPERTY_NAME,
            shape.fromY,
            shape.toY * mHeight
        )
        val viewXPropertyValueHolder = PropertyValuesHolder.ofFloat(
            VIEW_X_COEFFICIENT_PROPERTY_NAME,
            shape.fromX,
            1f
        )
        val viewYPropertyValueHolder = PropertyValuesHolder.ofFloat(
            VIEW_Y_COEFFICIENT_PROPERTY_NAME,
            shape.fromY * mHeight,
            1f
        )

        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            widthPropertyValueHolder,
            heightPropertyValueHolder,
            viewXPropertyValueHolder,
            viewYPropertyValueHolder
        ).apply {
            this.duration = this@AnimatedToolbar.duration
            this.interpolator = this@AnimatedToolbar.interpolator as BaseInterpolator
            addUpdateListener {
                path = shape.getPath(
                    it.getAnimatedValue(WIDTH_PROPERTY_NAME) as Float,
                    it.getAnimatedValue(HEIGHT_PROPERTY_NAME) as Float
                )

                viewAnimationListener?.onUpdate(
                    it.getAnimatedValue(VIEW_X_COEFFICIENT_PROPERTY_NAME) as Float,
                    it.getAnimatedValue(VIEW_Y_COEFFICIENT_PROPERTY_NAME) as Float
                )

                //TODO: elevation for api 17
                if (isApi21) {
                    outlineProvider = AnimatedToolbarOutlineProvider(path)
                }
                invalidate()
            }
            addListener(
                onStart = { toolbarAnimationListener?.onAnimationStart() },
                onEnd = { toolbarAnimationListener?.onAnimationEnd() }
            )
        }
        objectAnimator.start()
    }

    internal fun setViewAnimationListener(l: ViewAnimationUpdateListener) {
        viewAnimationListener = l
    }

    private fun getDefaultActionBarSize(): Int {
        TypedValue().apply {
            context.theme.resolveAttribute(android.R.attr.actionBarSize, this, true)
            return TypedValue.complexToDimensionPixelSize(this.data, displayMetrics)
        }
    }

    private fun setGradientColor() {
        if (this::gradientColor.isInitialized) {
            gradientColor.apply {
                this.width = mWidth.toFloat()
                this.height = mHeight.toFloat()
                paint.shader = this.getShader()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h =
            if (heightMode == MeasureSpec.EXACTLY) MeasureSpec.getSize(heightMeasureSpec) else max(
                toolbarHeight,
                heightMeasureSpec
            )
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        pathAnimation()
        setGradientColor()
    }
}