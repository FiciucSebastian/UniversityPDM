package com.example.ficiapp.components.data

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.ficiapp.components.data.remote.ComponentApi
import com.example.ficiapp.core.Properties
import com.example.ficiapp.core.Result
import com.example.ficiapp.core.TAG
import kotlinx.coroutines.launch

object ComponentRepoHelper {
    var componentRepository: ComponentRepository? = null
    private var component: Component? = null
    private var viewLifecycleOwner: LifecycleOwner? = null

    fun setComponentRepo(componentParam: ComponentRepository) {
        this.componentRepository = componentParam
    }

    fun setComponent(componentParam: Component) {
        this.component = componentParam
    }

    fun setViewLifecycleOwner(viewLifecycleOwnerParam: LifecycleOwner) {
        viewLifecycleOwner = viewLifecycleOwnerParam
    }

    fun save() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            saveHelper()
        }
    }

    private suspend fun saveHelper(): Result<Component> {
        try {
            if (Properties.instance.internetActive.value!!) {

                val createdComponent = ComponentApi.service.create(ComponentDTO(
                    component?.name!!,
                    component?.quantity!!,
                    component?.releaseDate!!,
                    component?.inStock!!,
                    component?.owner,
                ))

                createdComponent.action = ""
                componentRepository!!.componentDao.deleteComponent(createdComponent.name, createdComponent.releaseDate)
                componentRepository!!.componentDao.insert(createdComponent)
                Properties.instance.toastMessage.postValue("Component was saved on the server")
                return Result.Success(createdComponent)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun update() {
        viewLifecycleOwner!!.lifecycleScope.launch {
            updateHelper()
        }
    }

    private suspend fun updateHelper(): Result<Component> {
        try {
            if (Properties.instance.internetActive.value!!) {
                Log.d(TAG, "updateNewVersionHelper")
                component!!.action = ""
                val updatedComponent = ComponentApi.service.update(component!!._id, component!!)
                componentRepository!!.componentDao.update(updatedComponent)
                Properties.instance.toastMessage.postValue("Component was updated on the server")
                return Result.Success(updatedComponent)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun delete(){
        viewLifecycleOwner!!.lifecycleScope.launch {
            deleteHelper()
        }
    }

    private suspend fun deleteHelper(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                Log.d(TAG, "updateNewVersionHelper")
                ComponentApi.service.delete(component!!._id)
                componentRepository!!.componentDao.delete(component!!._id)
                Properties.instance.toastMessage.postValue("Component was deleted on the server")
                return Result.Success(true)
            } else {
                Log.d(TAG, "internet still not working...")
                return Result.Error(Exception("internet still not working..."))
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}