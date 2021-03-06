package com.muralex.achiever.domain.group_usecases

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject


class DeleteGroupUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun execute(group: Group, items: List<DataItem>) = repository.deleteGroup(group, items)
}