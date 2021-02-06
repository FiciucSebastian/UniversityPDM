package com.example.beforeexam.auth.data

import android.content.Context
import android.content.SharedPreferences
import com.example.beforeexam.auth.data.remote.RemoteAuthDataSource
import com.example.beforeexam.core.Api
import com.example.beforeexam.core.Result

object AuthRepository {

    var prefs: SharedPreferences? = null

    var user: User? = null
        private set

    init {
        user = null
    }

    fun logout() {
        user = null
        Api.tokenInterceptor.token = null
    }

    suspend fun login(name: String, context: Context): Result<Role> {
        val user = User(name)
        val result = RemoteAuthDataSource.login(user)

        if (prefs == null)
            prefs = context.getSharedPreferences("com.example.beforeexam", Context.MODE_PRIVATE)
        val editor = prefs!!.edit()
        editor.putString("username", name)
        editor.apply()

        return result
    }

    fun isLoggedIn(context: Context): Boolean {
        if (prefs == null)
            prefs = context.getSharedPreferences("com.example.beforeexam", Context.MODE_PRIVATE)
        val token = prefs!!.getString("token", "")!!
        if (token != "")
            Api.tokenInterceptor.token = token

        return token != ""
    }

    fun getUsername(): String = prefs!!.getString("username", "")!!
}