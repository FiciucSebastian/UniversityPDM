package com.example.ficiapp.components.componentList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ficiapp.components.data.Component
import com.example.ficiapp.components.data.ComponentRepoHelper
import com.example.ficiapp.components.data.ComponentRepository
import com.example.ficiapp.components.data.local.ComponentDatabase
import com.example.ficiapp.components.data.remote.RemoteDataSource
import com.example.ficiapp.core.TAG
import com.example.ficiapp.core.Result
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ComponentListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val componentRepository: ComponentRepository
    var components: LiveData<List<Component>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    init {
        val componentDao = ComponentDatabase.getDatabase(application).componentDao()
        componentRepository = ComponentRepository(componentDao)
        components = componentRepository.components

        ComponentRepoHelper.setComponentRepo(componentRepository)

        val request = Request.Builder().url("ws://192.168.0.52:3000").build()
        OkHttpClient().newWebSocket(
            request,
            RemoteDataSource.MyWebSocketListener(application.applicationContext)
        )
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = componentRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }

    private suspend fun collectEvents() {
        while (true) {
            val res = JSONObject(RemoteDataSource.eventChannel.receive())
            val component = Gson().fromJson(res.getJSONObject("payload").toString(), Component::class.java)
            Log.d("ws", "received $component")
            refresh()
        }
    }
}