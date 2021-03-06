package com.ehsanmsz.animatedtoolbarsample.ui.main.list

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ehsanmsz.animatedtoolbarsample.R
import com.ehsanmsz.animatedtoolbarsample.model.Student
import com.ehsanmsz.animatedtoolbarsample.ui.main.list.adapter.StudentAdapter
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment(R.layout.fragment_list) {

    private var itemClickListener: OnItemClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemAdapter = StudentAdapter(
            students = createItemList(20),
            onClick = { student, imageView -> itemClickListener?.onItemClick(student, imageView) }
        )

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }

    private fun createItemList(size: Int): List<Student> =
        mutableListOf<Student>().apply {
            for (i in 1..size) {
                add(Student(i, getColor(i)))
            }
        }

    private fun getColor(i: Int): Int =
        intArrayOf(
            0xfff44336.toInt(),
            0xffff9800.toInt(),
            0xff9c27b0.toInt(),
            0xff2196f3.toInt(),
            0xff607d8b.toInt(),
            0xff3f51b5.toInt(),
            0xff009688.toInt(),
            0xffffeb3b.toInt()
        ).let { it[(i - 1) % it.size] }

    fun setOnItemClickListener(l: OnItemClickListener) {
        itemClickListener = l
    }

    fun interface OnItemClickListener {
        fun onItemClick(student: Student, imageView: ImageView)
    }
}