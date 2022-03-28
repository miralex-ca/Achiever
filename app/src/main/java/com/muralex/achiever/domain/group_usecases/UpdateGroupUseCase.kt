package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun invoke(group: Group) = repository.updateGroup(group)
}

