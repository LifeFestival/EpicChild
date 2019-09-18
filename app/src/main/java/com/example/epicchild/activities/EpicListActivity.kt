package com.example.epicchild.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.adapters.EpicAdapter
import com.example.epicchild.dataBase.AppDatabase
import com.example.epicchild.dataBase.Epic
import com.example.epicchild.dataBase.Task
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_epic.*
import kotlinx.android.synthetic.main.activity_epic_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpicListActivity : AppCompatActivity() {

    private lateinit var epicListAdapter: EpicAdapter
    private lateinit var appDataBase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epic_list)

        AndroidThreeTen.init(this)

        appDataBase = AppDatabase.getInstance(this)

        GlobalScope.launch{

            withContext(Dispatchers.IO) {
                insertTestData(5)
                initAdapter()
            }
        }
    }

    private suspend fun initAdapter() {

        val testList = appDataBase.epicDao().getAllEpics()

        epicListAdapter = EpicAdapter(testList, this)
        epic_list_recycler.adapter = epicListAdapter
        epic_list_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        epic_list_recycler.layoutManager = LinearLayoutManager(this)

    }

    private suspend fun insertTestData(dataAmount: Int) {
        for (i in 0..dataAmount) {
            val name = "Epic number ${i + 1}"
            appDataBase.epicDao().insertEpic(Epic(name, ""))
        }
    }
}
