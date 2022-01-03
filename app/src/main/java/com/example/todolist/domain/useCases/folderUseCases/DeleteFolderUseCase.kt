package com.example.todolist.domain.useCases.folderUseCases

import com.example.todolist.domain.models.components.Folder
import com.example.todolist.util.Resource

class DeleteFolderUseCase {

    suspend operator fun invoke(folder: Folder): Resource<Unit> =
        folder.delete()

}