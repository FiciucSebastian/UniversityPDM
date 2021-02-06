package com.example.beforeexam.profesor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.beforeexam.R
import com.example.beforeexam.core.TAG
import com.example.beforeexam.profesor.remote.QuizRepoHelper
import kotlinx.android.synthetic.main.profesor_fragment.*

class ProfesorListFragment : Fragment() {
    private lateinit var profesorListAdapter: ProfesorListAdapter
    private lateinit var viewModel: ProfesorListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profesor_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupComponentList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        QuizRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
    }

    private fun setupComponentList() {
        profesorListAdapter = ProfesorListAdapter()
        item_list.adapter = profesorListAdapter
        viewModel = ViewModelProvider(this).get(ProfesorListViewModel::class.java)

        viewModel.quizes.observe(viewLifecycleOwner) { component ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${component.size}")
            profesorListAdapter.quizez = component
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
}