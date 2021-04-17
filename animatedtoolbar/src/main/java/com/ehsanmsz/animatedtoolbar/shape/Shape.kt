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
import androidx.annotation.Px

abstract class Shape {

    companion object {
        const val GRAVITY_LEFT = 0
        const val GRAVITY_RIGHT = 1
    }

    @Px
    var fromX = 1f
        set(value) {
            field = when {
                value > 1f -> 1f
                value < 0f -> 0f
                else -> value
            }
        }

    @Px
    var toX = 1f
        set(value) {
            field = when {
                value > 1f -> 1f
                value < 0f -> 0f
                else -> value
            }
        }

    @Px
    var fromY = 0f
        set(value) {
            field = when {
                value > 1f -> 1f
                value < 0f -> 0f
                else -> value
            }
        }

    @Px
    var toY = 1f
        set(value) {
            field = when {
                value > 1f -> 1f
                value < 0f -> 0f
                else -> value
            }
        }

    abstract fun getPath(vararg arg: Float): Path
}