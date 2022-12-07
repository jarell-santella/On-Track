package com.android.on_track.data.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskRepository(private val dao: TaskDatabaseDao) {
    val tasks: Flow<List<Task>> = dao.getTasks()

    fun insert(task: Task) {
        CoroutineScope(IO).launch {
            dao.insert(task)
        }
    }

    fun updateProgress(id: Int, progress: Int) {
        CoroutineScope(IO).launch {
            dao.updateProgress(id, progress)
        }
    }

    fun delete(id: Int) {
        CoroutineScope(IO).launch {
            dao.delete(id)
        }
    }
}