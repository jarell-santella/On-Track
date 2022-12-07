package com.android.on_track.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {
    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Query("UPDATE task_table SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Int, progress: Int)

    @Query("DELETE FROM task_table WHERE id = :id")
    suspend fun delete(id: Int)
}