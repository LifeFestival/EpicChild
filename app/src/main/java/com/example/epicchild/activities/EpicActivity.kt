package com.example.epicchild.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.epicchild.dataBase.Epic
import com.example.epicchild.viewModels.EpicViewModel
import kotlinx.android.synthetic.main.activity_epic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EpicActivity : AppCompatActivity() {

    companion object {
        private const val EPIC_ID = "epicId"

        fun newIntent(context: Context, epicId: UUID) =
            Intent(context, EpicActivity::class.java).putExtra(EPIC_ID, epicId)
    }

    private lateinit var epicId: UUID
    private lateinit var epic: Epic

    //Диалог
    private lateinit var taskCreatingDialog: AlertDialog

    private lateinit var tasksAdapter: EpicAdapter

    private val viewModel by viewModels<EpicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epic)

        epicId = intent.getSerializableExtra(EPIC_ID) as UUID

        viewModel.viewModelScope.launch {
            epic = viewModel.getEpic(epicId)
            withContext(Dispatchers.Main) {
                setEpicInfo(epic)
            }

            makeTaskCreatingDialog()
            setListeners()
            initAdapter()

        }
    }

    private fun setEpicInfo(epic: Epic) {
        epic_name_textView.text = epic.name
        epic_description_editText.setText(epic.description)
    }

    private fun setListeners() {
        epic_description_editText.onFocusChangeListener =
            View.OnFocusChangeListener { _, isFocus ->
                if (!isFocus) {
                    val changedEpic = epic.copy(description = epic_description_editText.text.toString())
                    viewModel.viewModelScope.launch {
                        viewModel.updateEpic(changedEpic)
                    }
                }
            }

        epic_add_button.setOnClickListener {
            taskCreatingDialog.show()
        }
    }

    private fun initAdapter() {
        tasksAdapter = EpicAdapter(listOf(), this::taskItemClick, this::taskItemLongClick)

        epic_recycler.adapter = tasksAdapter
        epic_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        epic_recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun taskItemClick(u: UUID) {}

    private fun taskItemLongClick() {
        enterDeletingMode()
    }

    //TODO удаление тасок
    private fun enterDeletingMode() {
        showToast("Режим удаления")
    }

    private fun makeTaskCreatingDialog() {
        val builder = AlertDialog.Builder(this)

        taskCreatingDialog = builder.setTitle("Название Задания")
            .setView(layoutInflater.inflate(R.layout.dialog_task_creating, epic_root, false))
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()

        taskCreatingDialog.setOnShowListener {
            val editText = taskCreatingDialog.findViewById<EditText>(R.id.task_dialog_editText)
            editText?.setText("")

            val button = taskCreatingDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val taskName = editText?.text.toString()

                if (taskName.isEmpty()) {
                    showToast("Имя задания не может быть пустым")
                } else {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        runCatching { viewModel.saveTask(taskName, epicId) }
                            .onSuccess {
                                taskCreatingDialog.dismiss()
                            }
                            .onFailure {
                                withContext(Dispatchers.Main) { showToast("Задание не было создано: ${it.localizedMessage}") }
                                taskCreatingDialog.dismiss()
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