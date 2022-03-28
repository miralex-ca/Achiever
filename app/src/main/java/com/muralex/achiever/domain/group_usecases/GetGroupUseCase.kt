package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class GetGroupUseCase @Inject constructor(private val repository: DataRepository) {
    fun invoke(id: String) = repository.getGroup(id)
}