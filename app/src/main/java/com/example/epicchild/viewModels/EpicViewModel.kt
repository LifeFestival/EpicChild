package com.example.epicchild.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.epicchild.dataBase.AppDatabase
import com.example.epicchild.dataBase.Epic
import com.example.epicchild.dataBase.Task
import java.util.*

class EpicViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase: AppDatabase = AppDatabase.getInstance(getApplication())

    suspend fun getEpic(epicId: UUID) = appDatabase.epicDao().getEpicById(epicId)

    suspend fun updateEpic(epic: Epic) = appDatabase.epicDao().updateEpic(epic)

    suspend fun saveTask(taskName: String, epicId: UUID) {
        val newTask = Task(
            taskName,
            epicId
        )
        appDatabase.taskDao().insertTask(newTask)
    }

}