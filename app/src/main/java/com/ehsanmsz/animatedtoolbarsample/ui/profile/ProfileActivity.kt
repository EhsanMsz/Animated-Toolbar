package com.ehsanmsz.animatedtoolbarsample.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ehsanmsz.animatedtoolbarsample.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var studentName = ""
    private var profileColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        checkExtras()

        text.text = studentName
        imageView.setColorFilter(profileColor)

        setSupportActionBar(toolbar)
    }

    private fun checkExtras() {
        intent.extras?.let {
            studentName = it[STUDENT_NAME] as String
            profileColor = it[STUDENT_PROFILE_COLOR] as Int
        }
    }

    companion object {
        const val STUDENT_NAME = "student_name"
        const val STUDENT_PROFILE_COLOR = "student_profile_color"
    }
}