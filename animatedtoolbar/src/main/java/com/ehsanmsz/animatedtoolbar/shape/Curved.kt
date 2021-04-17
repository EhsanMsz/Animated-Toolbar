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

class Curved : Shape {

    private var gravity: Int = GRAVITY_LEFT
    private val rect = RectF()
    private var radius: Float = -1f

    constructor(gravity: Int = GRAVITY_LEFT) {
        this.gravity = gravity
    }

    constructor(radius: Float, gravity: Int = GRAVITY_LEFT) {
        this.gravity = gravity
        this.radius = radius
    }


    override fun getPath(vararg arg: Float): Path {
        return Path().apply {
            moveTo(0f, 0f)
            if (gravity == GRAVITY_LEFT) {
                lineTo(arg[0], 0f)
                if (radius >= 0f)
                    rect.set(arg[0] - radius, arg[1] - radius, arg[0], arg[1])
                else
                    rect.set(0f, 0f, arg[0], arg[1])

                arcTo(rect, 0f, 90f, false)
                lineTo(0f, arg[1])
            } else if (gravity == GRAVITY_RIGHT) {
                lineTo(arg[0], 0f)
                lineTo(arg[0], arg[1])
                if (radius >= 0f) {
                    lineTo(radius, arg[1])
                    rect.set(0f, arg[1] - radius, radius, arg[1])
                } else
                    rect.set(0f, 0f, arg[0], arg[1])
                arcTo(rect, 90f, 90f, false)
            }
            close()
        }
    }
}