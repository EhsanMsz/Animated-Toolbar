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

class Rounded : Shape() {

    private var rightRect = RectF()
    private var leftRect = RectF()

    @Px
    var radius = 100f

    override fun getPath(width: Float, height: Float): Path {
        return Path().apply {
            rightRect.set(width - radius, height - radius, width, height)
            leftRect.set(0f, height - radius, radius, height)
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(width, height - radius)
            arcTo(rightRect, 0f, 90f, false)
            lineTo(radius, height)
            arcTo(leftRect, 90f, 90f, false)
            lineTo(0f, 0f)
            close()
        }
    }
}