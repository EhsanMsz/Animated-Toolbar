package com.ehsanmsz.animatedtoolbarsample.ui.main.list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ehsanmsz.animatedtoolbarsample.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_student.view.*

class StudentViewHolder(
    override val containerView: View,
    private val onClick: (student: Student) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(student: Student) {
        itemView.text.text = student.getName()
        itemView.imageView.setColorFilter(student.profileColor)
        itemView.setOnClickListener { onClick(student) }
    }
}