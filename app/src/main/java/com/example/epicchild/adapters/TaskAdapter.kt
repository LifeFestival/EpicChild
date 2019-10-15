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
    private val itemLongClick: (isDeleting: Boolean) -> Unit,
    private val itemFinished: (taskId: UUID, isChecked: Boolean, position: Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val mTaskList = taskList.toMutableList()
    private val mTaskDeletionList = mutableListOf<Task>()

    var isDeletingMode = false

    val elements
    get() = mTaskList.toList()

    val deletingElements
    get() = mTaskDeletionList.toList()

    override fun getItemCount(): Int = mTaskList.count()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val element = mTaskList[position]

        holder.nameTextView.text = element.name
        holder.finishCheckBox.isChecked = element.isFinished

        holder.root.setBackgroundResource(R.drawable.background_normal)

        holder.root.setOnLongClickListener {

            isDeletingMode = true

            holder.root.setBackgroundResource(R.drawable.background_purple_borders)
            mTaskDeletionList.add(element)

            itemLongClick(isDeletingMode)
            true
        }

        holder.finishCheckBox.setOnClickListener {
            itemFinished(element.id, !element.isFinished, position)
            mTaskList[position].isFinished = !element.isFinished
        }

        holder.root.setOnClickListener {
            if (isDeletingMode && !mTaskDeletionList.contains(element)) {
                mTaskDeletionList.add(element)
                holder.root.setBackgroundResource(R.drawable.background_purple_borders)
            } else if (isDeletingMode && mTaskDeletionList.contains(element)) {
                mTaskDeletionList.remove(element)
                holder.root.setBackgroundResource(R.drawable.background_normal)
            }
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

    fun deleteElements() {
        mTaskDeletionList.forEach { task ->
            if (mTaskList.contains(task)) {
                mTaskList.remove(task)
            }
        }

        disableDeletingMode()
    }

    fun disableDeletingMode() {
        isDeletingMode = false
        mTaskDeletionList.clear()

        notifyDataSetChanged()

    }

}