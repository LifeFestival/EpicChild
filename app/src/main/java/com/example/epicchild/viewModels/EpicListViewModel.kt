package com.example.epicchild.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.epicchild.dataBase.AppDatabase
import com.example.epicchild.dataBase.Epic
import com.jakewharton.threetenabp.AndroidThreeTen

class EpicListViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var appDatabase: AppDatabase

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
    }

    //TODO убрать генерацию
    suspend fun insertTestData(dataAmount: Int): List<Epic> {

        appDatabase.epicDao().deleteAllEpics()

        for (i in 0..dataAmount) {
            val name = "Epic number ${i + 1}"
            appDatabase.epicDao().insertEpic(Epic(name, ""))
        }

        return appDatabase.epicDao().getAllEpics()
    }

}