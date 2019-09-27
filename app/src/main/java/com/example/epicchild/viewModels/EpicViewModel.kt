package com.example.epicchild.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.epicchild.dataBase.AppDatabase
import com.example.epicchild.dataBase.Epic
import com.example.epicchild.dataBase.Task
import java.util.*

class EpicViewModel(application: Application) : AndroidViewModel(application) {

    //Лайв дата
    private val _taskLiveData = MutableLiveData<Task>()
    val taskLiveData: LiveData<Task>
        get() = _taskLiveData

    private val appDatabase: AppDatabase = AppDatabase.getInstance(getApplication())

    suspend fun getEpic(epicId: UUID) = appDatabase.epicDao().getEpicById(epicId)

    suspend fun getAllTasks(epicId: UUID) = appDatabase.taskDao().getTaskByEpicId(epicId)

    suspend fun updateEpic(epic: Epic) = appDatabase.epicDao().updateEpic(epic)

    suspend fun saveNewTask(taskName: String, epicId: UUID) {
        val newTask = Task(
            taskName,
            epicId
        )
        val epic = getEpic(epicId)
        epic.numberOfTasks += 1

        appDatabase.taskDao().insertTask(newTask)
        appDatabase.epicDao().updateEpic(epic)
        _taskLiveData.postValue(newTask)
    }

}