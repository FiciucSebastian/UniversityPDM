package com.example.ficiapp.components.componentList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ficiapp.R
import com.example.ficiapp.auth.data.AuthRepository
import com.example.ficiapp.components.data.ComponentRepoHelper
import com.example.ficiapp.components.data.ComponentRepoWorker
import com.example.ficiapp.core.Properties
import com.example.ficiapp.core.TAG
import kotlinx.android.synthetic.main.fragment_component_list.*

class ComponentListFragment : Fragment() {
    private lateinit var componentListAdapter: ComponentListAdapter
    private lateinit var viewModel: ComponentListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_component_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn(requireContext())) {
            Log.d(TAG, "is not logged in")
            findNavController().navigate(R.id.fragment_login)
            return
        }
        setupComponentList()
        fab.setOnClickListener {
            Log.v(TAG, "add new component")
            findNavController().navigate(R.id.fragment_edit_component)
        }
        logoutBtn.setOnClickListener {
            Log.v(TAG, "log out")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ComponentRepoHelper.setViewLifecycleOwner(viewLifecycleOwner)
        Properties.instance.internetActive.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "sending offline actions to server")
            sendOfflineActionsToServer() })
    }

    private fun sendOfflineActionsToServer() {
        val components = viewModel.componentRepository.componentDao.getAllComponents(AuthRepository.getUsername())
        components.forEach { component ->
            if (component.action == null) {
                component.action = ""
            }
            if (component.action != "") {
                Log.d(TAG, "${component.name} needs ${component.action}")
                ComponentRepoHelper.setComponent(component)
                var dataParam = Data.Builder().putString("operation", "save")
                when(component.action) {
                    "update" -> {
                        dataParam = Data.Builder().putString("operation", "update")
                    }
                    "delete" -> {
                        dataParam = Data.Builder().putString("operation", "delete")
                    }
                }
                val request = OneTimeWorkRequestBuilder<ComponentRepoWorker>()
                    .setInputData(dataParam.build())
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(request)
            }
        }
    }

    private fun setupComponentList() {
        componentListAdapter = ComponentListAdapter(this)
        item_list.adapter = componentListAdapter
        viewModel = ViewModelProvider(this).get(ComponentListViewModel::class.java)

        viewModel.components.observe(viewLifecycleOwner) { component ->
            Log.v(TAG, "update items")
            Log.d(TAG, "setupItemList items length: ${component.size}")
            componentListAdapter.components = component.filter { it.action != "delete" }
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

        search.doOnTextChanged { _, _, _, _ ->
            viewModel.components.observe(viewLifecycleOwner, { component ->
                componentListAdapter.components = component
                componentListAdapter.components =
                    componentListAdapter.searchAndFilter(search.text.toString(), inStock.isChecked, notInStock.isChecked)
                componentListAdapter.notifyDataSetChanged()
            })
        }

        inStock.setOnClickListener {
            if(inStock.isChecked) notInStock.isChecked = false
            viewModel.components.observe(viewLifecycleOwner, { component ->
                componentListAdapter.components = component
                componentListAdapter.components =
                    componentListAdapter.searchAndFilter(search.text.toString(), inStock.isChecked, notInStock.isChecked)
                componentListAdapter.notifyDataSetChanged()
            })
        }

        notInStock.setOnClickListener {
            if(notInStock.isChecked) inStock.isChecked = false
            viewModel.components.observe(viewLifecycleOwner, { component ->
                componentListAdapter.components = component
                componentListAdapter.components =
                    componentListAdapter.searchAndFilter(search.text.toString(), inStock.isChecked, notInStock.isChecked)
                componentListAdapter.notifyDataSetChanged()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
    }
}