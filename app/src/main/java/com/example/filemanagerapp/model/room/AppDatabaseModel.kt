package com.example.filemanagerapp.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FileEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabaseModel: RoomDatabase() {
    abstract fun getDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseModel? = null

        fun getDatabase(context: Context): AppDatabaseModel {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseModel::class.java, "notify_database"
                ).build()
                INSTANCE = instance
                return instance }
        }
    }
}