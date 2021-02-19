package com.example.exam.repo

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.exam.core.Properties
import com.example.exam.core.Result
import com.example.exam.core.TAG
import com.example.exam.model.Task
import com.example.exam.model.remote.TaskApi
import kotlinx.coroutines.launch

object TaskRepoHelper {
    var taskRepository: TaskRepository? = null
    private var task: Task? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    fun setTaskRepo(taskParam: TaskRepository) {
        this.taskRepository = taskParam
    }

    fun setTask(taskParam: Task) {
        this.task = taskParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun update() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            updateHelper()
        }
    }

    private suspend fun updateHelper(): Result<Task> {
        try {
            if (Properties.instance.internetActive.value!!) {
                Log.d(TAG, "updateNewVersionHelper")
                task!!.action = ""
                val updatedTask = TaskApi.service.update(task!!.id, task!!)
                taskRepository!!.taskDao.update(updatedTask)
                Properties.instance.toastMessage.postValue("Task was updated on the server")
                return Result.Success(updatedTask)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}