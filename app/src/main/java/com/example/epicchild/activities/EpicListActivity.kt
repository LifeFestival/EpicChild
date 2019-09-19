package com.example.epicchild.activities

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epicchild.R
import com.example.epicchild.adapters.EpicAdapter
import com.example.epicchild.viewModels.EpicListViewModel
import kotlinx.android.synthetic.main.activity_epic_list.*
import kotlinx.coroutines.*

class EpicListActivity : AppCompatActivity() {

    //Компоненты
    private lateinit var epicListAdapter: EpicAdapter
    private val viewModel by viewModels<EpicListViewModel>()

    //Диалог
    private lateinit var epicCreatingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epic_list)

        viewModel.viewModelScope.launch {
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
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()

        epicCreatingDialog.setOnShowListener {
            val editText = epicCreatingDialog.findViewById<EditText>(R.id.epic_dialog_editText)
            editText?.setText("")

            val button = epicCreatingDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val epicName = epicCreatingDialog.findViewById<EditText>(R.id.epic_dialog_editText)
                    ?.text.toString()

                if (epicName.isEmpty()) {
                    showToast("Имя эпика не может быть пустым")
                } else {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        runCatching { viewModel.saveEpic(epicName) }
                            .onSuccess {
                                withContext(Dispatchers.Main) { showToast("Эпик создан") }
                                epicCreatingDialog.dismiss()
                            }
                            .onFailure {
                                withContext(Dispatchers.Main) { showToast("Эпик не был создан") }
                                epicCreatingDialog.dismiss()
                            }
                    }
                }
            }
        }
    }

    private fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }
}
