package com.android.on_track.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.on_track.data.tasks.Task
import com.android.on_track.data.tasks.TaskRepository

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    val tasks: LiveData<List<Task>> = repository.tasks.asLiveData()

    fun insert(task: Task) {
        repository.insert(task)
    }

    fun updateProgress(id: Int, progress: Int) {
        repository.updateProgress(id, progress)
    }

    fun delete(id: Int) {
        repository.delete(id)
    }
}