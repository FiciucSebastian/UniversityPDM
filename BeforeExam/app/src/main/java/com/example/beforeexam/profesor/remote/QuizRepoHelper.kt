package com.example.beforeexam.profesor.remote

import androidx.lifecycle.LifecycleOwner
import com.example.beforeexam.profesor.model.Quiz

object QuizRepoHelper {
    var quizRepository: QuizRepository? = null
    private var quiz: Quiz? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    fun setQuizRepo(quizParam: QuizRepository) {
        this.quizRepository = quizParam
    }

    fun setQuiz(quizParam: Quiz) {
        this.quiz = quizParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }
}