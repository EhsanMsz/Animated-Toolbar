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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class AnimatedToolbarLayout : ConstraintLayout, ViewAnimationUpdateListener {

    private var mView: View? = null
    private var mAnimatedToolbar: AnimatedToolbar? = null

    private var viewX = 0f
    private var viewY = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
            context,
            attrs,
            defStyleAttr,
            0
    )

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        checkForChildView()
        setViewAnimationListener()
        mView?.also {
            viewX = if (it.x != 0f) it.x else viewX
            viewY = if (it.y != 0f) it.y else viewY
        }
    }

    private fun checkForChildView() {
        if (childCount > 2)
            throw RuntimeException("AnimatedToolbarLayout can host two direct child (including AnimatedToolbar)")

        for (i in 0 until childCount) {
            getChildAt(i).also {
                if (it is AnimatedToolbar) mAnimatedToolbar = it else mView = it
            }
        }

        if (mAnimatedToolbar == null)
            throw RuntimeException("AnimatedToolbarLayout must contain AnimatedToolbar")

    }

    private fun setViewAnimationListener() {
        mAnimatedToolbar!!.setViewAnimationListener(this)
    }

    override fun onUpdate(xCoefficient: Float, yCoefficient: Float) {
        mView?.apply {
            x = xCoefficient * viewX
            y = yCoefficient * viewY
        }
    }
}