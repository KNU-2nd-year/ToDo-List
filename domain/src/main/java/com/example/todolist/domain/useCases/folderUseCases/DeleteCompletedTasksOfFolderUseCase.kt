package com.example.todolist.domain.useCases.folderUseCases

import com.example.todolist.domain.models.components.Folder
import com.example.todolist.domain.util.Resource

class DeleteCompletedTasksOfFolderUseCase {

    suspend operator fun invoke(folder: Folder): Resource<Unit, Folder.FolderExceptions> {
        TODO("get root folder")
        //return folder.deleteCompletedTasks()
    }


}