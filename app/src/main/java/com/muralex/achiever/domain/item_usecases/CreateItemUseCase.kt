package com.muralex.achiever.domain.item_usecases

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.domain.DataRepository
import javax.inject.Inject

class CreateItemUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun execute(dataItem: DataItem) = repository.createDataItem(dataItem)
}