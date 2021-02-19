package com.example.exam.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.exam.R
import com.example.exam.model.Task
import kotlinx.android.synthetic.main.fragment_edit_task.*
import java.util.*

class TaskEditFragment : Fragment() {
    private lateinit var viewModel: TaskEditViewModel
    private var task: Task? = null
    private var attemptAt: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task = arguments?.getParcelable("task")
        task?.let {
            attemptAt = Date().time
            text.setText(task?.text)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        update_button.setOnClickListener {
            task?.let {
                it.text = text.text.toString()
                it.attemptUpdateAt = attemptAt
                viewModel.saveOrUpdateTask(it)
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(TaskEditViewModel::class.java)
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        val id = task?.id
        if (id == null) {
            task = Task(
                "",
                "",
                "",
                0,
                "",
                0
            )
        } else {
            viewModel.getTaskById(id).observe(viewLifecycleOwner, {
                if (it != null) {
                    task = it
                    text.setText(it.text)
                }
            })
        }
    }
}