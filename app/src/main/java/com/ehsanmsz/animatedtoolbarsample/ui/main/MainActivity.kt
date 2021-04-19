package com.ehsanmsz.animatedtoolbarsample.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.ehsanmsz.animatedtoolbar.AnimatedToolbar
import com.ehsanmsz.animatedtoolbar.color.GradientColor
import com.ehsanmsz.animatedtoolbarsample.R
import com.ehsanmsz.animatedtoolbarsample.model.Student
import com.ehsanmsz.animatedtoolbarsample.ui.main.list.ListFragment
import com.ehsanmsz.animatedtoolbarsample.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val color = GradientColor(
            intArrayOf(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorSecondary)
            ), 90.0
        )

        toolbar.apply {
            duration = 400L
            //interpolator = DecelerateInterpolator()
            //shape = Curved(100, ShapeGravity.RIGHT)
            gradientColor = color
            toolbarAnimationListener = object : AnimatedToolbar.ToolbarAnimationListener {
                override fun onAnimationStart() {
                    println("AnimatedToolbar: started")
                }

                override fun onAnimationEnd() {
                    println("AnimatedToolbar: ended")
                }
            }
        }

        setSupportActionBar(toolbar)
        setListFragment()
    }

    private fun setListFragment() {
        val listFragment = ListFragment()
        listFragment.setOnItemClickListener { student, imageView ->
            startProfileActivity(student, imageView)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, listFragment).commit()
    }

    private fun startProfileActivity(student: Student, imageView: ImageView) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imageView,
            getString(R.string.profile_image_view)
        )
        startActivity(Intent(this, ProfileActivity::class.java).apply {
            putExtra(ProfileActivity.STUDENT_NAME, student.getName())
            putExtra(ProfileActivity.STUDENT_PROFILE_COLOR, student.profileColor)
        }, options.toBundle())
    }
}
