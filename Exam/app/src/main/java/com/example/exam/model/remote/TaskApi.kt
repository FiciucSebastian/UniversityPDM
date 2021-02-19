package com.example.exam.model.remote

import com.example.exam.core.Api
import com.example.exam.model.Task
import retrofit2.http.*

object TaskApi {
    interface Service {
        @GET("/task")
        suspend fun find(): List<Task>

        @GET("/task/{id}")
        suspend fun read(@Path("id") taskId: String): Task

        @Headers("Content-Type: application/json")
        @PUT("/task/{id}")
        suspend fun update(@Path("id") taskId: String, @Body task: Task): Task
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}