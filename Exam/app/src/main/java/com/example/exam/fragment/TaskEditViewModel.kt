package com.example.exam.fragment

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.example.exam.repo.TaskRepository
import com.example.exam.core.Result
import com.example.exam.core.Properties
import com.example.exam.model.Task
import com.example.exam.model.local.TaskDatabase
import kotlinx.coroutines.launch

class TaskEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableException = MutableLiveData<Exception>().apply { value = null }
    val fetchingError: LiveData<Exception> = mutableException

    val taskRepository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        taskRepository = TaskRepository(taskDao)
    }

    fun getTaskById(taskId: String): LiveData<Task> {
        return taskRepository.getById(taskId)
    }

    fun saveOrUpdateTask(task: Task) {
        viewModelScope.launch {
            mutableException.value = null
            when (val result: Result<Task> = taskRepository.update(task)) {
                is Result.Error -> {
                    if(result.exception.message?.contains("409")!!){
                        Properties.instance.toastMessage.postValue("There are version conflicts. Please try again")
                    }
                    mutableException.value = result.exception
                }
            }
        }
    }
}