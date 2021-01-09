package com.example.ficiapp.components.data.remote

import com.example.ficiapp.components.data.Component
import com.example.ficiapp.components.data.ComponentDTO
import com.example.ficiapp.core.Api
import retrofit2.Response
import retrofit2.http.*

object ComponentApi {
    interface Service {
        @GET("/api/component")
        suspend fun find(): List<Component>

        @GET("/api/component/{id}")
        suspend fun read(@Path("id") componentId: String): Component

        @Headers("Content-Type: application/json")
        @POST("/api/component")
        suspend fun create(@Body component: ComponentDTO): Component

        @Headers("Content-Type: application/json")
        @PUT("/api/component/{id}")
        suspend fun update(@Path("id") componentId: String, @Body component: Component): Component

        @DELETE("/api/component/{id}")
        suspend fun delete(@Path("id") componentId: String): Response<Unit>
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}