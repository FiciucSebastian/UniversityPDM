package com.example.ficiapp.components.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ficiapp.components.data.Component

@Dao
interface ComponentDao {
    @Query("SELECT * from components WHERE owner=:username ORDER BY name ASC")
    fun getAll(username: String): LiveData<List<Component>>

    @Query("SELECT * from components WHERE owner=:username ORDER BY name ASC")
    fun getAllComponents(username: String): List<Component>

    @Query("SELECT * FROM components WHERE _id=:id ")
    fun getById(id: String): LiveData<Component>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(component: Component)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(component: Component)

    @Query("DELETE FROM components")
    suspend fun deleteAll()

    @Query("DELETE FROM components WHERE _id=:id ")
    suspend fun delete(id: String)

    @Query("DELETE FROM components WHERE name=:name and releaseDate=:releaseDate")
    suspend fun deleteComponent(name: String, releaseDate: String)
}