package com.example.epicchild.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.epicchild.dataBase.AppDatabase
import java.util.*

class EpicViewModel(application: Application) : AndroidViewModel(application) {

    private val appDatabase: AppDatabase = AppDatabase.getInstance(getApplication())

    suspend fun getEpic(epicId: UUID) = appDatabase.epicDao().getEpicById(epicId)

}