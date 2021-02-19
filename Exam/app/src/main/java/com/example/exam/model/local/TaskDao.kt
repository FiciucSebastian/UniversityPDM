package com.example.exam.model.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.exam.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * from tasks")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * from tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id=:id ")
    fun getById(id: String): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: Task)
}