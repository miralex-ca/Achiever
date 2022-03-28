package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class GetGroupsListUseCase @Inject constructor(private val repository: DataRepository) {
    fun invoke() = repository.getGroupsList()
}