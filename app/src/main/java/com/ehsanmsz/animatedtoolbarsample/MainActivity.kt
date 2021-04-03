package com.ehsanmsz.animatedtoolbarsample

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ehsanmsz.animatedtoolbar.AnimatedToolbar
import com.ehsanmsz.animatedtoolbar.color.GradientColor
import com.ehsanmsz.animatedtoolbar.shape.Rect
import com.ehsanmsz.animatedtoolbar.shape.RoundedRect
import com.ehsanmsz.animatedtoolbar.shape.Shape

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animatedToolbar = findViewById<AnimatedToolbar>(R.id.toolbar)

        val rect = Rect().apply {
            angle = 30.0
            gravity = Shape.GRAVITY_RIGHT
        }

        val roundedRect = RoundedRect().apply {
            radius = 70f
        }

        val color = GradientColor(
            intArrayOf(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorSecondary)
            ),
            90.0
        )

        animatedToolbar.apply {
            duration = 400L
            //interpolator = DecelerateInterpolator()
            //shape = roundedRect
            //shape = rect
            gradientColor = color
            toolbarAnimationListener = object : AnimatedToolbar.ToolbarAnimationListener {
                override fun onAnimationStart() {
                    println("animation started")
                }

                override fun onAnimationEnd() {
                    println("animation ended")
                }
            }
        }

        setSupportActionBar(animatedToolbar)
    }
}