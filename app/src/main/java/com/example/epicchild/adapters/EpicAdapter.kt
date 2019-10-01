package com.example.epicchild.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.dataBase.Epic
import java.util.*

class EpicAdapter(
    epicList: List<Epic>,
    private val itemClick: (epicId: UUID) -> Unit,
    private val itemLongClick: () -> Unit
) : RecyclerView.Adapter<EpicAdapter.EpicViewHolder>() {

    private val mEpicList = epicList.toMutableList()

    override fun getItemCount(): Int = mEpicList.count()

    override fun onBindViewHolder(holder: EpicViewHolder, position: Int) {

        val element = mEpicList[position]
        val counterString = "${element.numberOfCompletedTasks}/${element.numberOfTasks}"

        holder.nameTextView.text = element.name
        holder.counterTextView.text = counterString

        when {
            element.numberOfCompletedTasks == 0 -> holder.iconView.setImageResource(R.drawable.ic_sleeping)
            element.numberOfCompletedTasks == element.numberOfTasks -> holder.iconView.setImageResource(R.drawable.ic_done_outline)

            else -> holder.iconView.setImageResource(R.drawable.ic_rowing)
        }

        //Listeners
        holder.root.apply {
            setOnClickListener { itemClick(element.id) }
            setOnLongClickListener {
                itemLongClick
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpicViewHolder {
        return EpicViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.epic_item_layout,
                parent,
                false
            )
        )
    }

    inner class EpicViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = view.findViewById(R.id.epic_item_name_textView)
        val counterTextView: TextView = view.findViewById(R.id.epic_item_counter_textView)
        val iconView: ImageView = view.findViewById(R.id.epic_item_complete_imageView)
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