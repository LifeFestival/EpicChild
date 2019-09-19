package com.example.epicchild.activities

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epicchild.R
import com.example.epicchild.adapters.EpicAdapter
import com.example.epicchild.viewModels.EpicListViewModel
import kotlinx.android.synthetic.main.activity_epic_list.*
import kotlinx.coroutines.*

class EpicListActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    //Компоненты
    private lateinit var epicListAdapter: EpicAdapter
    private val viewModel by viewModels<EpicListViewModel>()

    //Диалог
    private lateinit var epicCreatingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epic_list)

        GlobalScope.launch(Dispatchers.Main) {

            initAdapter()
            makeEpicCreatingDialog()
            setListeners()
        }
    }

    private suspend fun initAdapter() {

        withContext(Dispatchers.IO) {
            val testList = viewModel.insertTestData(10)
            epicListAdapter = EpicAdapter(testList, this@EpicListActivity)
        }

        epic_list_recycler.adapter = epicListAdapter
        epic_list_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        epic_list_recycler.layoutManager = LinearLayoutManager(this)

    }

    private fun setListeners() {
        epic_list_add_button.setOnClickListener {
            epicCreatingDialog.show()
        }
    }


    private fun makeEpicCreatingDialog() {
        val builder = AlertDialog.Builder(this)

        epicCreatingDialog = builder.setTitle("Название Эпика")
            .setView(layoutInflater.inflate(R.layout.dialog_epic_creating, epic_list_root, false))
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->

                val epicName = epicCreatingDialog.findViewById<EditText>(R.id.epic_dialog_editText)?.text.toString()

                if (epicName.isEmpty()) {
                    showToast("Имя эпика не может быть пустым")
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        runCatching { viewModel.saveEpic(epicName) }
                            .onSuccess {
                                showToast("Эпик создан")
                                dialogInterface.dismiss()
                            }
                            .onFailure {
                                showToast("Эпик не был создан")
                                dialogInterface.dismiss()
                            }
                    }
                }
            }
            .create()
    }

    private fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
