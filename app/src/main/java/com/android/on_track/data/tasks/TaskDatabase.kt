package com.android.on_track.data.tasks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class], version = 1)
@TypeConverters(TaskDateConverters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract val dao: TaskDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context) : TaskDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, TaskDatabase::class.java, "task_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}