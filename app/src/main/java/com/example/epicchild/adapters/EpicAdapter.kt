package com.example.epicchild.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.epicchild.R
import com.example.epicchild.dataBase.Epic

class EpicAdapter(private val epicList: List<Epic>, private val context: Context) : RecyclerView.Adapter<EpicAdapter.EpicViewHolder>() {

    override fun getItemCount(): Int = epicList.count()

    override fun onBindViewHolder(holder: EpicViewHolder, position: Int) {

        val element = epicList[position]
        val counterString = "${element.numberOfCompletedTasks}/${element.numberOfTasks}"

        holder.nameTextView.text = element.name
        holder.counterTextView.text = counterString
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpicViewHolder {
        return EpicViewHolder(LayoutInflater.from(context).inflate(R.layout.epic_item_layout, parent, false))
    }

    inner class EpicViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener { Toast.makeText(context, "Выбрал эпик!", Toast.LENGTH_SHORT).show() }
        }

        val nameTextView: TextView = view.findViewById(R.id.epic_item_name_textView)
        val counterTextView: TextView = view.findViewById(R.id.epic_item_counter_textView)

    }
}