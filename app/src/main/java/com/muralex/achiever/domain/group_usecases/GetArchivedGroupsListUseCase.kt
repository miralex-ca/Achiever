package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class GetArchivedGroupsListUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun invoke() = repository.getArchivedGroupsList()
}