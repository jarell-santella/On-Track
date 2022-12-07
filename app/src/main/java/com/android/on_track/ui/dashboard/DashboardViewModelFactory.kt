package com.android.on_track.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.data.tasks.TaskRepository

class DashboardViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return DashboardViewModel(repository) as T
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unknown VideoModel class")
        }
    }
}