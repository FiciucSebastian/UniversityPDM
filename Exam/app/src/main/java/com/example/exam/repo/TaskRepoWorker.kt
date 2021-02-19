package com.example.exam.repo

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskRepoWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        when (inputData.getString("operation")) {
            "update" -> TaskRepoHelper.update()
            else -> return Result.failure()
        }
        return Result.success()
    }
}