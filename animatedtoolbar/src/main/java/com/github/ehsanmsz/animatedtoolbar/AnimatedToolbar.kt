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
package com.github.ehsanmsz.animatedtoolbar

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
import com.github.ehsanmsz.animatedtoolbar.color.GradientColor
import com.github.ehsanmsz.animatedtoolbar.shape.Rect
import com.github.ehsanmsz.animatedtoolbar.shape.Shape
import kotlin.math.max

class AnimatedToolbar : Toolbar {

    companion object {
        private const val WIDTH_PROPERTY_NAME = "width"
        private const val HEIGHT_PROPERTY_NAME = "height"
    }

    private var mWidth = 0
    private var mHeight = 0
    private var mColor: Int = 0

    @Px
    var toolbarHeight: Int = 0

    lateinit var gradientColor: GradientColor
    var interpolator: Any = DecelerateInterpolator()
    var duration = 400L //milliseconds
    var shape: Shape = Rect()

    private lateinit var paint: Paint
    private var displayMetrics: DisplayMetrics? = null
    private var path = Path()
    private var isApi21 = false

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
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
            shape.fromY * mHeight,
            shape.toY * mHeight
        )

        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            widthPropertyValueHolder,
            heightPropertyValueHolder
        ).apply {
            this.duration = this@AnimatedToolbar.duration
            this.interpolator = this@AnimatedToolbar.interpolator as BaseInterpolator
            addUpdateListener {

                path = shape.getPath(
                    it.getAnimatedValue(WIDTH_PROPERTY_NAME) as Float,
                    it.getAnimatedValue(HEIGHT_PROPERTY_NAME) as Float
                )
                //TODO: elevation for api 17
                if (isApi21) {
                    outlineProvider = AnimatedToolbarOutlineProvider(path)
                }
                invalidate()
            }
        }
        objectAnimator.start()
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