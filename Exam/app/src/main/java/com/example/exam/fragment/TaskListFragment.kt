package com.example.exam.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.exam.R
import com.example.exam.core.Properties
import com.example.exam.core.TAG
import com.example.exam.repo.TaskRepoHelper
import com.example.exam.repo.TaskRepoWorker
import kotlinx.android.synthetic.main.fragment_task_list.*

class TaskListFragment : Fragment() {
    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var viewModel: TaskListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTaskList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TaskRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        Properties.instance.internetActive.observe(viewLifecycleOwner, {
            Log.d(TAG, "sending offline actions to server")
            sendOfflineActionsToServer() })
    }

    private fun sendOfflineActionsToServer() {
        val tasks = viewModel.taskRepository.taskDao.getAllTasks()
        tasks.forEach { task ->
            if (task.action == null) {
                task.action = ""
            }
            if (task.action != "") {
                TaskRepoHelper.setTask(task)
                var dataParam = Data.Builder().putString("operation", "save")
                when(task.action) {
                    "update" -> {
                        dataParam = Data.Builder().putString("operation", "update")
                    }
                }
                val request = OneTimeWorkRequestBuilder<TaskRepoWorker>()
                    .setInputData(dataParam.build())
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(request)
            }
        }
    }

    private fun setupTaskList() {
        taskListAdapter = TaskListAdapter(this)
        item_list.adapter = taskListAdapter
        viewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)

        viewModel.tasks.observe(viewLifecycleOwner) { task ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${task.size}")
            taskListAdapter.tasks = task.filter { it.action != "delete" }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loadingError.observe(viewLifecycleOwner) { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}