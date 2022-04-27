package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class GetGroupWithItemsUseCase @Inject constructor(private val repository: DataRepository) {
    fun execute(id: String) = repository.getGroupWithDataItems(id)
}