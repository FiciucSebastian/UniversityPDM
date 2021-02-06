package com.example.beforeexam.auth.data.remote

import com.example.beforeexam.auth.data.Role
import com.example.beforeexam.auth.data.User
import com.example.beforeexam.core.Api
import com.example.beforeexam.core.Result
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object RemoteAuthDataSource {
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("/auth")
        suspend fun login(@Body user: User): Role
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<Role> {
        try {
            return Result.Success(authService.login(user))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}