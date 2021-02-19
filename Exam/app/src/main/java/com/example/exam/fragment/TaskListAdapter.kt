package com.example.exam.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.exam.R
import com.example.exam.model.Task
import kotlinx.android.synthetic.main.view_task.view.*

class TaskListAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    var tasks = emptyList<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onTaskClick: View.OnClickListener

    init {
        onTaskClick = View.OnClickListener { view ->
            val task = view.tag as Task
            fragment.findNavController()
                .navigate(
                    R.id.action_TaskListFragment_to_TaskEditFragment,
                    bundleOf("task" to task)
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = tasks.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tag: TextView = view.task_tag
        private val text: TextView = view.task_text
        private val version: TextView = view.task_version

        fun bind(holder: ViewHolder, position: Int) {
            val task = tasks[position]

            with(holder) {
                itemView.tag = task
                tag.text = task.tag
                text.text = task.text
                version.text = task.version.toString()
                itemView.setOnClickListener(onTaskClick)
            }
        }
    }
}