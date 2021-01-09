package com.example.ficiapp.components.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.ficiapp.auth.data.AuthRepository
import com.example.ficiapp.components.data.local.ComponentDao
import com.example.ficiapp.components.data.remote.ComponentApi
import com.example.ficiapp.core.Properties
import com.example.ficiapp.core.Result

class ComponentRepository(val componentDao: ComponentDao) {

    var components = MediatorLiveData<List<Component>>().apply { postValue(emptyList()) }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val componentsApi = ComponentApi.service.find()
                components.value = componentsApi
                for (component in componentsApi) {
                    component.owner = AuthRepository.getUsername()
                    componentDao.insert(component)
                }
            } else
                components.addSource(componentDao.getAll(AuthRepository.getUsername())) {
                    components.value = it
                }
            return Result.Success(true)
        } catch (e: Exception) {
            components.addSource(componentDao.getAll(AuthRepository.getUsername())) {
                components.value = it
            }
            return Result.Error(e)
        }
    }

    fun getById(componentId: String): LiveData<Component> {
        return componentDao.getById(componentId)
    }

    suspend fun save(component: Component): Result<Component> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val createdComponent = ComponentApi.service.create(
                    ComponentDTO(
                        component.name,
                        component.quantity,
                        component.releaseDate,
                        component.inStock,
                        component.owner,
                    )
                )
                createdComponent.action = ""
                componentDao.insert(createdComponent)
                return Result.Success(createdComponent)
            } else {
                component.action = "save"
                componentDao.insert(component)
                Properties.instance.toastMessage.postValue("Component was saved locally. It will be saved on the server once you connect to the internet")
                return Result.Success(component)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(component: Component): Result<Component> {
        try {
            if (Properties.instance.internetActive.value!!) {
                val updatedComponent = ComponentApi.service.update(component._id, component)
                updatedComponent.action = ""
                componentDao.update(updatedComponent)
                return Result.Success(updatedComponent)
            }
            else {
                component.action = "update"
                componentDao.update(component)
                Properties.instance.toastMessage.postValue("Component was updated locally. It will be updated to the server once you connect to the internet")
                return Result.Success(component)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(component: Component): Result<Boolean> {
        try {
            if (Properties.instance.internetActive.value!!) {

                ComponentApi.service.delete(component._id)
                componentDao.delete(component._id)
                return Result.Success(true)
            }
            else{
                component.action = "delete"
                componentDao.update(component)
                Properties.instance.toastMessage.postValue("Component was deleted locally. It will be deleted to the server once you connect to the internet")
                return Result.Success(true)
            }
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}