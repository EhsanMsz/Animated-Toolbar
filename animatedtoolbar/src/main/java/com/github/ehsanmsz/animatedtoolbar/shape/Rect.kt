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
package com.github.ehsanmsz.animatedtoolbar.shape

import android.graphics.Path
import kotlin.math.sin

class Rect : Shape() {

    var gravity = GRAVITY_LEFT
    var angle: Double = 30.0
        set(value) {
            if (value > 90.0) field = 90.0
            if (value < 0.0) field = 0.0
            field = value
        }

    override fun getPath(vararg arg: Float): Path {
        return Path().apply {
            moveTo(0f, 0f)
            lineTo(arg[0], 0f)
            if (gravity == GRAVITY_LEFT) {
                lineTo(arg[0], getHeight(arg[1]))
                lineTo(0f, arg[1])
            } else if (gravity == GRAVITY_RIGHT) {
                lineTo(arg[0], arg[1])
                lineTo(0f, getHeight(arg[1]))
            }
            lineTo(0f, 0f)
            close()
        }
    }

    /**
     * calculates height using angle
     *
     * @param height max height
     * @see angle
     *
     * @return height
     * */
    private fun getHeight(height: Float): Float {
        return height * sin(Math.toRadians(angle)).toFloat()
    }
}