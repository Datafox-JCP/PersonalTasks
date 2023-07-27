package mx.datafox.personaltasks.task_list

import mx.datafox.personaltasks.data.Task

sealed class TaskListEvent {
    data class OnDeleteTaskClicked(val task: Task): TaskListEvent()
    data class OnCompletedChange(val task: Task, val completed: Boolean): TaskListEvent()
    object OnUndoDeleteClicked: TaskListEvent()
    data class OnTaskClicked(val task: Task): TaskListEvent()
    object OnAddTaskCLicked: TaskListEvent()
}
