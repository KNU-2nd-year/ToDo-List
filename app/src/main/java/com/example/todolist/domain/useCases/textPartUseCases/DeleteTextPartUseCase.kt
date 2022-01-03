package com.example.todolist.domain.useCases.textPartUseCases

import com.example.todolist.domain.models.parts.TextPart
import com.example.todolist.domain.repositories.PartsRepository
import com.example.todolist.util.Resource
import javax.inject.Inject

class DeleteTextPartUseCase @Inject constructor(
    private val partsRepository: PartsRepository
) {

    suspend operator fun invoke(textPart: TextPart): Resource<Unit> =
        partsRepository.deleteTextPart(textPart)

}