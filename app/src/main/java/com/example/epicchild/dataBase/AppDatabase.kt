package com.example.epicchild.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.epicchild.dataBase.dao.EpicDao
import com.example.epicchild.dataBase.dao.TaskDao

@TypeConverters(Converter::class)
@Database(entities = [Epic::class, Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "epic_data_base").build()
        }
    }

    abstract fun epicDao(): EpicDao
    abstract fun taskDao(): TaskDao

}