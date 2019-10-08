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
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.adapters.TaskAdapter
import com.example.epicchild.dataBase.Epic
import com.example.epicchild.dataBase.Task
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

    //Обсервер
    private val newTaskObserver = Observer<Task> {
        if (it == null) return@Observer

        tasksAdapter.add(it)
    }

    private val epicUpdateObserver = Observer<Epic> {
        if (it == null) return@Observer

        epic = it
    }

    private lateinit var tasksAdapter: TaskAdapter

    private val viewModel by viewModels<EpicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epic)

        epicId = intent.getSerializableExtra(EPIC_ID) as UUID

        viewModel.viewModelScope.launch {
            epic = viewModel.getEpic(epicId)
            withContext(Dispatchers.Main) {
                setEpicInfo(epic)
                initAdapter()
            }

            makeTaskCreatingDialog()
            setListeners()
        }

        viewModel.taskLiveData.observe(this, newTaskObserver)
        viewModel.epicLiveData.observe(this, epicUpdateObserver)
    }

    private fun setEpicInfo(epic: Epic) {
        epic_name_textView.text = epic.name
        epic_description_editText.setText(epic.description)
    }

    private fun setListeners() {
        epic_description_editText.onFocusChangeListener =
            View.OnFocusChangeListener { _, isFocus ->
                if (!isFocus) {
                    val changedEpic =
                        epic.copy(description = epic_description_editText.text.toString())
                    viewModel.viewModelScope.launch {
                        viewModel.updateEpic(changedEpic)
                    }
                }
            }

        task_add_button.setOnClickListener {
            taskCreatingDialog.show()
        }

        epic_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && task_add_button.visibility == View.VISIBLE) task_add_button.hide()
                else if (dy < 0 && task_add_button.visibility != View.VISIBLE) task_add_button.show()
            }
        })
    }

    private suspend fun initAdapter() {

        withContext(Dispatchers.IO) {
            val taskList = viewModel.getAllTasks(epicId)
            tasksAdapter = TaskAdapter(
                taskList,
                this@EpicActivity::enterDeletingMode,
                this@EpicActivity::setTaskFinished
            )
        }

        epic_recycler.adapter = tasksAdapter
        epic_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        epic_recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun enterDeletingMode() {
        tasksAdapter.enterDeletingMode()
        floatDeletingMode(true)
    }

    private fun deleteTasks() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {

            val taskList = tasksAdapter.deletingElements

            withContext(Dispatchers.Main) {
                tasksAdapter.deleteElements()
            }

            viewModel.deleteTasks(taskList)

            floatDeletingMode(false)
        }
    }

    private fun floatDeletingMode(isDeletingMode: Boolean) {
        if (!isDeletingMode) {
            task_add_button.setImageResource(R.drawable.ic_add)
            task_add_button.setOnClickListener { taskCreatingDialog.show() }
        } else {
            task_add_button.setImageResource(R.drawable.ic_delete)
            task_add_button.setOnClickListener { deleteTasks() }
        }
    }

    private fun setTaskFinished(taskId: UUID, isFinished: Boolean, position: Int) {
        viewModel.viewModelScope.launch {
            val task = viewModel.getTaskById(taskId)
            task.isFinished = isFinished

            if (isFinished) epic.numberOfCompletedTasks += 1
            else epic.numberOfCompletedTasks -= 1

            viewModel.updateTask(task)
            viewModel.updateEpic(epic)

            tasksAdapter.notifyItemChanged(position)
        }
    }

    private fun makeTaskCreatingDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)

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
                        runCatching {
                            viewModel.saveNewTask(taskName, epicId)
                        }
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

    override fun onPause() {
        super.onPause()

        val desc = epic_description_editText.text.toString()

        epic.description = desc
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.updateEpic(epic)
        }
    }

    override fun onBackPressed() {
        if (tasksAdapter.isDeletingMode) {
            tasksAdapter.disableDeletingMode()
            floatDeletingMode(false)
        } else super.onBackPressed()
    }
}