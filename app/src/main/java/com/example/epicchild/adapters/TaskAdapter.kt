package com.example.epicchild.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.dataBase.Task
import java.util.*


class TaskAdapter(
    taskList: List<Task>,
    private val itemLongClick: () -> Unit,
    private val itemFinished: (taskId: UUID, isChecked: Boolean, position: Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val mTaskList = taskList.toMutableList()

    override fun getItemCount(): Int = mTaskList.count()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val element = mTaskList[position]

        holder.nameTextView.text = element.name
        holder.finishCheckBox.isChecked = element.isFinished

        holder.root.setOnLongClickListener {
            itemLongClick()
            true
        }

        holder.finishCheckBox.setOnClickListener {
            itemFinished(element.id, !element.isFinished, position)
            mTaskList[position].isFinished = !element.isFinished
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout, parent, false))


    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = view.findViewById(R.id.task_item_name_textView)
        val finishCheckBox: CheckBox = view.findViewById(R.id.task_item_checkBox)
        val root: ConstraintLayout = view.findViewById(R.id.task_item_root)
    }

    fun add(task: Task) {
        mTaskList.add(task)
        notifyDataSetChanged()
    }

    fun addAll(taskList: List<Task>) {
        mTaskList.clear()
        mTaskList.addAll(taskList)
        notifyDataSetChanged()
    }
}