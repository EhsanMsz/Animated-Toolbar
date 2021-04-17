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
package com.ehsanmsz.animatedtoolbar.color

import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.annotation.Px
import kotlin.math.cos
import kotlin.math.sin

class GradientColor(
    private var colors: IntArray,
    private var angle: Double = 0.0
) {
    @Px
    var width = 0f
    @Px
    var height = 0f

    private var x1 = 0f
    private var y1 = 0f

    fun getShader(): Shader {
        setCoordinates()
        return LinearGradient(0f, 0f, x1, y1, colors, null, Shader.TileMode.CLAMP)
    }

    private fun setCoordinates() {
        x1 = (cos(Math.toRadians(angle)) * width).toFloat()
        y1 = (sin(Math.toRadians(angle)) * height).toFloat()
    }
}