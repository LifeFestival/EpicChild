package com.example.epicchild.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.epicchild.dataBase.AppDatabase
import com.example.epicchild.dataBase.Epic
import com.jakewharton.threetenabp.AndroidThreeTen

class EpicListViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var appDatabase: AppDatabase

    //Лайв дата
    private val _epicLiveData = MutableLiveData<Epic>()
    val epicLiveData: LiveData<Epic>
        get() = _epicLiveData

    init {
        initComponents()
    }

    private fun initComponents() {
        AndroidThreeTen.init(getApplication())
        appDatabase = AppDatabase.getInstance(getApplication())

    }

    suspend fun getEpics(): List<Epic> = appDatabase.epicDao().getAllEpics()

    suspend fun saveEpic(name: String) {

        val epic = Epic(name, "")

        appDatabase.epicDao().insertEpic(epic)
        _epicLiveData.postValue(epic)
    }

    suspend fun deleteEpic(epic: Epic) = appDatabase.epicDao().deleteEpic(epic)

    suspend fun deleteEpics(epicList: List<Epic>) {

        epicList.forEach { epic ->
            val tasks = appDatabase.taskDao().getTaskByEpicId(epic.id)

            appDatabase.taskDao().deleteTasks(tasks)
            appDatabase.epicDao().deleteEpic(epic)
        }
    }

}