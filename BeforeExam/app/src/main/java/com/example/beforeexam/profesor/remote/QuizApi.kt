package com.example.beforeexam.profesor.remote

import com.example.beforeexam.core.Api
import com.example.beforeexam.profesor.model.Quiz
import retrofit2.http.GET

object QuizApi {
    interface Service {
        @GET("/quiz")
        suspend fun find(): List<Quiz>
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}