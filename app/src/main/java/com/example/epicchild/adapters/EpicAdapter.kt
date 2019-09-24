package com.example.epicchild.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.dataBase.Epic
import java.util.*

class EpicAdapter(
    epicList: List<Epic>,
    private val context: Context,
    private val itemClickListener: (epicId: UUID) -> Unit,
    private val itemLongClickListener: () -> Unit
) : RecyclerView.Adapter<EpicAdapter.EpicViewHolder>() {

    private val mEpicList = epicList.toMutableList()

    override fun getItemCount(): Int = mEpicList.count()

    override fun onBindViewHolder(holder: EpicViewHolder, position: Int) {

        val element = mEpicList[position]
        val counterString = "${element.numberOfCompletedTasks}/${element.numberOfTasks}"

        holder.nameTextView.text = element.name
        holder.counterTextView.text = counterString

        //Listeners
        holder.root.apply {
            setOnClickListener { itemClickListener(element.id) }
            setOnLongClickListener {
                itemLongClickListener
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpicViewHolder {
        return EpicViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.epic_item_layout,
                parent,
                false
            )
        )
    }

    inner class EpicViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                Toast.makeText(context, "Выбрал эпик!", Toast.LENGTH_SHORT).show()
            }
        }

        val nameTextView: TextView = view.findViewById(R.id.epic_item_name_textView)
        val counterTextView: TextView = view.findViewById(R.id.epic_item_counter_textView)
        val root: ConstraintLayout = view.findViewById(R.id.epic_item_root)

    }

    fun add(epic: Epic) {
        mEpicList.add(epic)
        notifyDataSetChanged()
    }

    fun addAll(epicList: List<Epic>) {
        mEpicList.clear()
        mEpicList.addAll(epicList)
        notifyDataSetChanged()
    }
}