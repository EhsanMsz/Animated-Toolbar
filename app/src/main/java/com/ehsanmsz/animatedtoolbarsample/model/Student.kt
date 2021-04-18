package com.ehsanmsz.animatedtoolbarsample.model

data class Student(
    val id: Int,
    val profileColor: Int
) {
    fun getName() = "Student #$id"
}