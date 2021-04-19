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
import android.content.res.TypedArray
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
import androidx.core.content.ContextCompat
import com.ehsanmsz.animatedtoolbar.color.GradientColor
import com.ehsanmsz.animatedtoolbar.shape.*

class AnimatedToolbar : Toolbar {

    companion object {
        private const val WIDTH_PROPERTY_NAME = "width"
        private const val HEIGHT_PROPERTY_NAME = "height"
        private const val VIEW_X_COEFFICIENT_PROPERTY_NAME = "view_width"
        private const val VIEW_Y_COEFFICIENT_PROPERTY_NAME = "view_height"

        private const val DEFAULT_ANIMATION_DURATION = 400
        private const val DEFAULT_GRAVITY = 0 //ShapeGravity.LEFT
        private const val DEFAULT_TOOLBAR_SHAPE = 0 //Ramp
        private const val DEFAULT_COLOR_ANGLE = 90f
        private const val DEFAULT_RAMP_ANGLE = 30f
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
    private var isAnimationEnabled = true
    private var usingSharedElement = false

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
            duration = getInteger(
                R.styleable.AnimatedToolbar_duration,
                DEFAULT_ANIMATION_DURATION
            ).toLong()

            isAnimationEnabled = getBoolean(R.styleable.AnimatedToolbar_animationEnabled, true)
            usingSharedElement = getBoolean(R.styleable.AnimatedToolbar_usingSharedElement, false)
            initToolbarShape(this)
            initGradientColor(this)
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

    private fun initToolbarShape(ta: TypedArray) {
        val gravity = ta.getInteger(R.styleable.AnimatedToolbar_shapeGravity, DEFAULT_GRAVITY)
        val roundedRadius =
            ta.getDimensionPixelSize(
                R.styleable.AnimatedToolbar_roundedRadius,
                Rounded.DEFAULT_RADIUS
            )
        val curvedRadius =
            ta.getDimensionPixelSize(
                R.styleable.AnimatedToolbar_curvedRadius,
                Curved.DEFAULT_RADIUS
            )

        when (ta.getInteger(R.styleable.AnimatedToolbar_shape, DEFAULT_TOOLBAR_SHAPE)) {
            0 -> {
                shape = Ramp(
                    angle = ta.getFloat(
                        R.styleable.AnimatedToolbar_rampAngle,
                        DEFAULT_RAMP_ANGLE
                    ).toDouble(),
                    gravity = ShapeGravity.values()[gravity]
                )
            }
            1 -> {
                shape = Rounded(roundedRadius)
            }
            2 -> {
                shape = Curved(
                    radius = curvedRadius,
                    gravity = ShapeGravity.values()[gravity]
                )
            }
        }
    }

    private fun initGradientColor(ta: TypedArray) {
        val startColorId = ta.getResourceId(R.styleable.AnimatedToolbar_toolbarStartColor, 0)
        val endColorId = ta.getResourceId(R.styleable.AnimatedToolbar_toolbarEndColor, 0)
        val startColor = ta.getColor(R.styleable.AnimatedToolbar_toolbarStartColor, 0)
        val endColor = ta.getColor(R.styleable.AnimatedToolbar_toolbarEndColor, 0)

        val colorAngle =
            ta.getFloat(R.styleable.AnimatedToolbar_toolbarColorAngle, DEFAULT_COLOR_ANGLE)

        val start =
            if (startColorId != 0)
                ContextCompat.getColor(context, startColorId)
            else
                startColor

        val end =
            if (endColorId != 0)
                ContextCompat.getColor(context, endColorId)
            else
                endColor

        if (start != 0 && end != 0) {
            gradientColor = GradientColor(intArrayOf(start, end), colorAngle.toDouble())
        }
    }

    @SuppressLint("newApi")
    private fun getPrimaryColor(): Int {
        val attr = if (isApi21) android.R.attr.colorPrimary else R.attr.colorPrimary
        return TypedValue().apply {
            context.theme.resolveAttribute(attr, this, true)
        }.data
    }

    private fun updatePath() {
        if (isAnimationEnabled)
            animatePath()
        else
            setPath()
    }

    @SuppressLint("newApi")
    private fun animatePath() {
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

    @SuppressLint("NewApi")
    private fun setPath() {
        path = shape.getPath(shape.toX * mWidth, shape.toY * mHeight)

        //TODO: elevation for api 17
        if (isApi21) {
            outlineProvider = AnimatedToolbarOutlineProvider(path)
        }
        invalidate()
    }

    internal fun setViewAnimationListener(l: ViewAnimationUpdateListener) {
        setViewAnimationListenerIfNeeded(l)
    }

    private fun setViewAnimationListenerIfNeeded(l: ViewAnimationUpdateListener) {
        if (!usingSharedElement) viewAnimationListener = l
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
            if (heightMode == MeasureSpec.EXACTLY) MeasureSpec.getSize(heightMeasureSpec) else toolbarHeight
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
        updatePath()
        setGradientColor()
    }
}