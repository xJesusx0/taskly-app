package com.example.pruebaalmacenamiento.repositories

import com.example.pruebaalmacenamiento.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksFlow(): Flow<List<Task>>
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun toggleTaskCompleted(taskId: String)
}