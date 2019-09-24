package com.example.epicchild.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

            initAdapter()

        }
    }

    //TODO переделать
    private fun setEpicInfo(epic: Epic) {
        epic_name_textView.text = epic.name
        epic_description_editText.text = epic.description
    }

    private fun initAdapter() {
        tasksAdapter = EpicAdapter(listOf(), this, this::tt) {}

        epic_recycler.adapter = tasksAdapter
        epic_recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        epic_recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun tt(u: UUID) {}

}