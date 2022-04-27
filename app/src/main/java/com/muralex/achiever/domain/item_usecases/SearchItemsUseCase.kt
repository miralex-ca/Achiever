package com.muralex.achiever.domain.item_usecases

import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.domain.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchItemsUseCase  @Inject constructor(private val repository: DataRepository) {
    suspend fun execute(query: String): Flow<List<SearchItem>> = repository.searchItemsByQuery(query)
}