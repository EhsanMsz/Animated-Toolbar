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
package com.ehsanmsz.animatedtoolbar.shape

import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.Px

class Curved : Shape {

    companion object {
        const val DEFAULT_RADIUS = 100f
    }

    var gravity: ShapeGravity = ShapeGravity.LEFT
        private set

    @Px
    var radius: Float = DEFAULT_RADIUS
        private set(value) {
            field = if (value < 0) 0f else value
        }

    private val rect = RectF()

    constructor(gravity: ShapeGravity = ShapeGravity.LEFT) {
        this.gravity = gravity
    }

    constructor(@Px radius: Float, gravity: ShapeGravity = ShapeGravity.LEFT) {
        this.gravity = gravity
        this.radius = radius
    }

    override fun getPath(width: Float, height: Float): Path {
        return Path().apply {
            moveTo(0f, 0f)
            if (gravity == ShapeGravity.LEFT) {
                lineTo(width, 0f)
                rect.set(width - radius, height - radius, width, height)
                arcTo(rect, 0f, 90f, false)
                lineTo(0f, height)
            } else if (gravity == ShapeGravity.RIGHT) {
                lineTo(width, 0f)
                lineTo(width, height)
                lineTo(radius, height)
                rect.set(0f, height - radius, radius, height)
                arcTo(rect, 90f, 90f, false)
            }
            close()
        }
    }
}