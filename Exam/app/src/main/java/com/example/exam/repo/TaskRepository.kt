package com.example.exam.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.exam.model.local.TaskDao
import com.example.exam.core.Properties
import com.example.exam.core.Result
import com.example.exam.model.Task
import com.example.exam.model.remote.TaskApi

class TaskRepository(val taskDao: TaskDao) {

    var tasks = MediatorLiveData<List<Task>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val tasksApi = TaskApi.service.find()
                tasks.value = tasksApi
                for (task in tasksApi) {
                    taskDao.insert(task)
                }
            } else
                tasks.addSource(taskDao.getAll()) {
                    tasks.value = it
                }
            return Result.Success(true)
        } catch (e: Exception) {
            tasks.addSource(taskDao.getAll()) {
                tasks.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(taskId: String): LiveData<Task> {
        return taskDao.getById(taskId)
    }

    suspend fun update(task: Task): Result<Task> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val updatedTask = TaskApi.service.update(task.id, task)
                updatedTask.action = ""
                taskDao.update(updatedTask)
                return Result.Success(updatedTask)
            }
            else {
                task.action = "update"
                taskDao.update(task)
                Properties.instance.toastMessage.postValue("Task was updated locally. It will be updated to the server once you connect to the internet")
                return Result.Success(task)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}