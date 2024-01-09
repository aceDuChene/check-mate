package com.oukschub.checkmate.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.oukschub.checkmate.R
import com.oukschub.checkmate.data.database.Database
import com.oukschub.checkmate.data.model.Checklist

class HomeViewModel(
    private val database: Database = Database()
) : ViewModel() {
    private val _checklists = mutableStateListOf<Checklist>()
    val checklists: List<Checklist> = _checklists

    init {
        loadChecklistsFromDb()
    }

    fun loadChecklistsFromDb(onLoadSuccess: () -> Unit = {}) {
        database.loadChecklistsFromDb {
            _checklists.add(it)
            onLoadSuccess()
        }
    }

    fun changeChecklistTitle(
        title: String,
        index: Int
    ) {
        _checklists[index] = _checklists[index].copy(title = title)
    }

    fun sendChecklistTitle(
        title: String,
        index: Int,
        onComplete: (Int) -> Unit
    ) {
        if (title.isNotEmpty()) {
            database.updateChecklistInDb(
                checklist = _checklists[index],
                onSuccess = {
                    _checklists[index] = _checklists[index].copy(title = title)
                    onComplete(R.string.checklist_update)
                }
            )
        } else {
            onComplete(R.string.checklist_update_error)
        }
    }

    fun sendChecklistItem(
        name: String,
        onComplete: (Int) -> Unit
    ) {
        if (name.isNotEmpty()) {
            onComplete(R.string.checklist_update)
        } else {
            onComplete(R.string.checklist_update_error)
        }
    }
}
