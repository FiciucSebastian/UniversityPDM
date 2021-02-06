package com.example.beforeexam.profesor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beforeexam.R
import com.example.beforeexam.core.TAG
import com.example.beforeexam.profesor.model.Quiz
import kotlinx.android.synthetic.main.view_component.view.*

class ProfesorListAdapter : RecyclerView.Adapter<ProfesorListAdapter.ViewHolder>() {
    var quizez = emptyList<Quiz>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_component, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = quizez.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val id: TextView = view.idQuiz
        private val name: TextView = view.name

        fun bind(holder: ViewHolder, position: Int) {
            val quiz = quizez[position]

            with(holder) {
                itemView.tag = quiz
                id.text = quiz.id.toString()
                name.text = quiz.name
            }
        }
    }
}