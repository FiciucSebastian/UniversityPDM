package com.example.ficiapp.components.componentEdit

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.example.ficiapp.components.data.Component
import com.example.ficiapp.components.data.ComponentRepository
import com.example.ficiapp.components.data.local.ComponentDatabase
import com.example.ficiapp.core.TAG
import com.example.ficiapp.core.Result
import com.example.ficiapp.core.Properties
import kotlinx.coroutines.launch

class ComponentEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val componentRepository: ComponentRepository

    init {
        val componentDao = ComponentDatabase.getDatabase(application).componentDao()
        componentRepository = ComponentRepository(componentDao)
    }

    fun getComponentById(componentId: String): LiveData<Component> {
        Log.v(TAG, "getComponentById...")
        return componentRepository.getById(componentId)
    }

    fun saveOrUpdateComponent(component: Component) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateComponent...");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Component>
            if (component._id.isNotEmpty()) {
                result = componentRepository.update(component)
            } else {
                result = componentRepository.save(component)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded");
                }
                is Result.Error -> {
                    if(result.exception.message?.contains("409")!!){
                        Properties.instance.toastMessage.postValue("There are version conflicts. Please try again")
                    }
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun deleteItem(component: Component) {
        viewModelScope.launch {
            Log.v(TAG, "deleteItem...");
            val result = componentRepository.delete(component)
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "deleteItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "deleteItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
        }
    }
}