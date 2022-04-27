package com.muralex.achiever.domain.item_usecases

import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.domain.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetPinnedItemsUseCase @Inject constructor(private val repository: DataRepository) {
    suspend fun execute() : Flow<List<PinnedItem>> = repository.getPinnedItemsList()
}