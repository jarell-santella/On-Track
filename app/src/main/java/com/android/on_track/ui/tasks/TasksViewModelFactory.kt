package com.android.on_track.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.data.tasks.TaskRepository

class TasksViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return TasksViewModel(repository) as T
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unknown VideoModel class")
        }
    }
}