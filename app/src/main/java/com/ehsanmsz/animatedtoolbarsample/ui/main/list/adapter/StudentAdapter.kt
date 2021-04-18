package com.ehsanmsz.animatedtoolbarsample.ui.main.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ehsanmsz.animatedtoolbarsample.R
import com.ehsanmsz.animatedtoolbarsample.model.Student

class StudentAdapter(
    private val students: List<Student>,
    private val onClick: (student: Student) -> Unit
) : RecyclerView.Adapter<StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder =
        LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
            .let { StudentViewHolder(it, onClick) }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) =
        holder.bind(students[position])
}