package mx.datafox.personaltasks.task_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mx.datafox.personaltasks.R
import mx.datafox.personaltasks.data.Task
import mx.datafox.personaltasks.data.TaskRepository
import mx.datafox.personaltasks.utils.Routes
import mx.datafox.personaltasks.utils.UIEvent
import mx.datafox.personaltasks.utils.UIText
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks = repository.getTasks()

    private val _uIEvent = Channel<UIEvent>()
    val uiEvent = _uIEvent.receiveAsFlow()

    // Cache para la tarea eliminada
    private var deletedTask: Task? = null

    fun onEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.OnTaskClicked -> {
                sendUiEvent(UIEvent.Navigate(Routes.ADD_EDIT_TASK + "?todoId=${event.task.id}"))
            }
            is TaskListEvent.OnDeleteTaskClicked -> {
                viewModelScope.launch {
                    deletedTask = event.task
                    repository.deleteTask(event.task)
                    val message = UIText.StringResource(R.string.task_deleted).toString()
                    val action = UIText.StringResource(R.string.undo).toString()
                    sendUiEvent(UIEvent.ShowSnackbar(
                        message = message,
                        action = action
                    ))
                }
            }
            TaskListEvent.OnAddTaskCLicked -> {
                sendUiEvent(UIEvent.Navigate(Routes.ADD_EDIT_TASK))
            }
            is TaskListEvent.OnCompletedChange -> {
                viewModelScope.launch {
                    repository.insertTask(
                        event.task.copy(
                            isCompleted =  event.completed
                        )
                    )
                }
            }
            is TaskListEvent.OnUndoDeleteClicked -> {
                deletedTask?.let { task ->
                    viewModelScope.launch {
                        repository.insertTask(task)
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UIEvent) {
        viewModelScope.launch {
            _uIEvent.send(event)
        }
    }
}