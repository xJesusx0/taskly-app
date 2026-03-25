package com.example.pruebaalmacenamiento.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pruebaalmacenamiento.datastore.dataStore
import com.example.pruebaalmacenamiento.models.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Implementación con DataStore. Para migrar a SQLite/Room:
// 1. Crea RoomTaskRepository implementando TaskRepository
// 2. Cambia la inyección en el ViewModel/Screen
// 3. No toques nada más.

class DataStoreTaskRepository(private val context: Context) : TaskRepository {

    private val TASKS_KEY = stringPreferencesKey("tasks_json")
    private val json = Json { ignoreUnknownKeys = true }

    // Flow único compartido: cualquier edit() al DataStore lo emite automáticamente
    private val tasksFlow: Flow<List<Task>> =
        context.dataStore.data.map { prefs ->
            val raw = prefs[TASKS_KEY] ?: return@map emptyList()
            try {
                json.decodeFromString<List<Task>>(raw)
            } catch (e: Exception) {
                emptyList()
            }
        }

    override fun getAllTasksFlow(): Flow<List<Task>> = tasksFlow

    override suspend fun addTask(task: Task) = editTasks { it + task }

    override suspend fun updateTask(task: Task) = editTasks { tasks ->
        tasks.map { if (it.id == task.id) task else it }
    }

    override suspend fun deleteTask(taskId: String) = editTasks { tasks ->
        tasks.filter { it.id != taskId }
    }

    override suspend fun toggleTaskCompleted(taskId: String) = editTasks { tasks ->
        tasks.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
    }

    private suspend fun editTasks(transform: (List<Task>) -> List<Task>) {
        context.dataStore.edit { prefs ->
            val current = try {
                json.decodeFromString<List<Task>>(prefs[TASKS_KEY] ?: "[]")
            } catch (e: Exception) { emptyList() }
            prefs[TASKS_KEY] = json.encodeToString(transform(current))
        }
    }
}