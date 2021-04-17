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
import kotlin.math.sin

class Ramp(angle: Double = 30.0, var gravity: Int = GRAVITY_LEFT) : Shape() {

    var angle: Double = angle
        set(value) {
            field = when {
                value > 90.0 -> 90.0
                value < 0.0 -> 0.0
                else -> value
            }
        }

    override fun getPath(width: Float, height: Float): Path {
        return Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            if (gravity == GRAVITY_LEFT) {
                lineTo(width, getHeight(height))
                lineTo(0f, height)
            } else if (gravity == GRAVITY_RIGHT) {
                lineTo(width, height)
                lineTo(0f, getHeight(height))
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