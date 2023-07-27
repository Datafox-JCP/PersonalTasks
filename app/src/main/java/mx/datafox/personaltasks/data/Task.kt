package mx.datafox.personaltasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    @PrimaryKey val id: Int? = null
)
