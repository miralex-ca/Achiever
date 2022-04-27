package com.example.noter.domain.usecases

import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class GetItemUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun execute(id: String) = repository.getDataItem(id)
}