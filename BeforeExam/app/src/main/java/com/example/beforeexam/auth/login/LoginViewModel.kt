package com.example.beforeexam.auth.login

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beforeexam.R
import com.example.beforeexam.auth.data.AuthRepository
import com.example.beforeexam.auth.data.Role
import kotlinx.coroutines.launch
import com.example.beforeexam.core.Result
import com.example.beforeexam.core.TAG

class LoginViewModel : ViewModel() {

    private val mutableLoginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = mutableLoginFormState

    private val mutableLoginResult = MutableLiveData<Result<Role>>()
    val loginResult: LiveData<Result<Role>> = mutableLoginResult

    fun login(name: String, context: Context) {
        viewModelScope.launch {
            Log.v(TAG, "login...")
            mutableLoginResult.value = AuthRepository.login(name, context)
        }
    }

    fun loginDataChanged(name: String) {
        if (!isUserNameValid(name)) {
            mutableLoginFormState.value = LoginFormState(usernameError = R.string.invalid_username)
        }  else {
            mutableLoginFormState.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(name: String): Boolean {
        return if (name.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(name).matches()
        } else {
            name.isNotBlank()
        }
    }
}