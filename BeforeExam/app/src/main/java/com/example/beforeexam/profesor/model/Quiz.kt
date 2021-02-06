package com.example.beforeexam.profesor.model

import com.example.beforeexam.util.Question

data class Quiz(
    var id: Int,
    var name: String,
    var question: ArrayList<*>
)