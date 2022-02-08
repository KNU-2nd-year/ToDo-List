package com.example.todolist.data.components

import android.util.Log
import com.example.todolist.data.components.componentsLocalDataSource.FolderLocalDataSource
import com.example.todolist.data.components.componentsLocalDataSource.TaskLocalDataSource
import com.example.todolist.data.components.componentsLocalDataSource.entities.FolderDbModel
import com.example.todolist.data.components.componentsLocalDataSource.entities.TaskDbModel
import com.example.todolist.domain.models.components.Component
import com.example.todolist.domain.models.components.Folder
import com.example.todolist.domain.models.components.Task
import com.example.todolist.domain.repositories.ComponentsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComponentsRepositoryImpl(
    private val folderLocalDataSource: FolderLocalDataSource,
    private val taskLocalDataSource: TaskLocalDataSource,
) : ComponentsRepository {

    override fun getSubFoldersFlow(parentFolderId: Long): Flow<List<Folder>> {
        return folderLocalDataSource.getSubFoldersFlow(parentFolderId)
            .map { it.mapFolderListToDomain() }
    }

    override fun getSubTasksFlow(parentFolderId: Long): Flow<List<Task>> {
        return taskLocalDataSource.getSubTasksFlow(parentFolderId)
            .map { it.mapTaskListToDomain() }
    }

    override fun getFolderFlow(folderId: Long): Flow<Folder> {
        return folderLocalDataSource.getFolderFlow(folderId)
            .map { it.mapToDomain() }
    }

    override suspend fun getRootFolder(): Folder {
        Log.d("TAG", "getRootFolder: started!")
        var rootFolder =  folderLocalDataSource.getRootFolder()
        while(rootFolder == null) {
            Log.i("TAG", "getRootFolder: root folder is $rootFolder")
            delay(500)
            rootFolder = folderLocalDataSource.getRootFolder()
        }
        Log.d("TAG", "getRootFolder: finished!")
        return rootFolder.mapToDomain()
    }

    override suspend fun getStarredFolders(): List<Folder> {
        return folderLocalDataSource.getStarredFolders().mapFolderListToDomain()
    }

    override suspend fun getTask(taskId: Long): Task {
        return taskLocalDataSource.getTask(taskId).mapToDomain()
    }

    override suspend fun getFolder(folderId: Long): Folder {
        return folderLocalDataSource.getFolder(folderId).mapToDomain()
    }

    override suspend fun addTask(task: Task): Long {
        return taskLocalDataSource.addTask(task.mapToData())
    }

    override suspend fun addFolder(folder: Folder): Long {
        return folderLocalDataSource.addFolder(
            FolderDbModel(
                title = folder.title,
                parentFolderId = folder.parentFolderId,
                isStarred = folder.isStarred
            )
        )
    }

    override suspend fun updateTask(task: Task) {
        taskLocalDataSource.updateTask(task.mapToData())
    }

    override suspend fun updateFolder(folder: Folder) {
        folderLocalDataSource.updateFolder(folder.mapToData())
    }

    override suspend fun deleteTask(taskId: Long) {
        taskLocalDataSource.deleteTask(taskId)
    }

    override suspend fun deleteCompletedTasks() {
        taskLocalDataSource.deleteCompletedTasks()
    }

    override suspend fun deleteFolder(folderId: Long) {
        folderLocalDataSource.deleteFolder(folderId)
    }

    private suspend fun FolderDbModel.mapToDomain(): Folder {
        return Folder(
            title = title,
            parentFolderId = parentFolderId,
            isStarred = isStarred,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            id = id,
            subComponents = getSubComponents(id)
        )
    }

    private suspend fun getSubComponents(parentFolderId: Long): List<Component> {
        Log.d("TAG", "getSubComponents: started!")
        val subFoldersList = folderLocalDataSource.getSubFolders(parentFolderId)
        val foldersList = subFoldersList.mapFolderListToDomain()
        /*val foldersList = folderLocalDataSource
            .getSubFolders(parentFolderId)
            .mapFolderListToDomain()
            as List<Component>*/
        Log.d("TAG", "getSubComponents: first step completed!")
        val tasksList = taskLocalDataSource
            .getSubTasks(parentFolderId)
            .mapTaskListToDomain()
            as List<Component>

        Log.d("TAG", "getSubComponents: finished!")
        return foldersList.plus(tasksList)
    }

    private fun Folder.mapToData(): FolderDbModel {
        return FolderDbModel(
            title = title,
            parentFolderId = parentFolderId,
            isStarred = isStarred,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            id = id,
        )
    }

    private fun TaskDbModel.mapToDomain(): Task {
        return Task(
            title = title,
            parentFolderId = parentFolderId,
            isImportant = isImportant,
            isCompleted = isCompleted,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            id = id,
        )
    }

    private fun Task.mapToData(): TaskDbModel {
        return TaskDbModel(
            title = title,
            parentFolderId = parentFolderId,
            isImportant = isImportant,
            isCompleted = isCompleted,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            id = id,
        )
    }

    private suspend fun List<FolderDbModel>.mapFolderListToDomain(): List<Folder> {
        return this.map { it.mapToDomain() }
    }

    private fun List<TaskDbModel>.mapTaskListToDomain(): List<Task> {
        return this.map { it.mapToDomain() }
    }

}
