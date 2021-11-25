package com.example.todolist.ui.addEditTask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.componentsDB.FolderDao
import com.example.todolist.data.componentsDB.Task
import com.example.todolist.data.componentsDB.TaskDao
import com.example.todolist.data.repository.AppRepository
import com.example.todolist.ui.ADD_TASK_RESULT_OK
import com.example.todolist.ui.EDIT_TASK_RESULT_NOTHING_CHANGED
import com.example.todolist.ui.EDIT_TASK_RESULT_OK
import com.example.todolist.ui.entities.BasePart
import com.example.todolist.ui.entities.TextPart
import com.example.todolist.ui.entities.TodoPart
import com.example.todolist.util.exhaustive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val folderDao: FolderDao,
    private val appRepository: AppRepository,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val folderId = state.get<Long>("folderId")
    val task = state.get<Task>("task") ?: Task("", folderId!!)
    val isModifyingTask: Boolean
        get() = state.get<Task>("task") != null

    var taskName = state.get<String>("taskName") ?: task.title ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task.isImportant
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val partsFlow = appRepository.getPartsOfTaks(task.id)
    val parts = partsFlow.asLiveData()

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClicked(showNothingChangeMessage: Boolean = true) {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name can not be empty.")
            return
        }

        if (isModifyingTask) { // if the task exists
            if (state.get<Task>("task") != task) { // if some data of the task have changed
                val updatedTask = task.copy(
                    title = taskName,
                    isImportant = taskImportance,
                    modifiedDate = System.currentTimeMillis()
                )
                updateTask(updatedTask)
                updateFolder()
            } else if(showNothingChangeMessage) { // if nothing have changed
                viewModelScope.launch {
                    addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(
                        EDIT_TASK_RESULT_NOTHING_CHANGED))
                }
            }
        } else { // if it is a new task
            val newTask = Task(taskName, folderId!!, taskImportance)
            insertTask(newTask)
            updateFolder()
        }
    }

    fun onAddTextPartClicked() = viewModelScope.launch {
        appRepository.insertTextPart(TextPart("", parts.value?.size ?: 1, task.id))
        updateFolder()
    }

    fun onAddTodoPartClicked() = viewModelScope.launch {
        appRepository.insertTodoPart(TodoPart("", parts.value?.size ?: 1, task.id))
        updateFolder()
    }

    fun onPartContentChanged(part: BasePart) = viewModelScope.launch {
        when(part) {
            is TextPart -> appRepository.updateTextPart(part)
            is TodoPart -> appRepository.updateTodoPart(part)
            else -> throw IllegalArgumentException()
        }.exhaustive
        updateFolder()
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.updateTask(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun insertTask(task: Task) = viewModelScope.launch {
        taskDao.insertTask(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateFolder() = viewModelScope.launch {
        val folder = folderDao.getFolder(folderId!!)
        folderDao.updateFolder(folder.copy(modifiedDate = System.currentTimeMillis()))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}