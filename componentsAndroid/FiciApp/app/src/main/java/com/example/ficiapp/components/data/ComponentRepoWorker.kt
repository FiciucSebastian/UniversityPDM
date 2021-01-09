package com.example.ficiapp.components.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ComponentRepoWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        when (inputData.getString("operation")) {
            "save" -> ComponentRepoHelper.save()
            "update" -> ComponentRepoHelper.update()
            "delete" -> ComponentRepoHelper.delete()
            else -> return Result.failure()
        }
        return Result.success()
    }
}