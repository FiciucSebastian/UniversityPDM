package com.example.beforeexam.profesor.remote

import androidx.lifecycle.MediatorLiveData
import com.example.beforeexam.core.Properties
import com.example.beforeexam.core.Result
import com.example.beforeexam.profesor.model.Quiz

class QuizRepository {

    var quizes = MediatorLiveData<List<Quiz>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val componentsApi = QuizApi.service.find()
                quizes.value = componentsApi
            }
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}