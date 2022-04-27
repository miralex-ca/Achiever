package com.muralex.achiever.domain.item_usecases

import com.example.noter.domain.usecases.GetItemUseCase
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.domain.DataRepository
import com.muralex.achiever.utils.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

@ExperimentalCoroutinesApi
class GetItemUseCaseTest : BaseUnitTest() {

    private val repository = mock<DataRepository>()
    private val getItemUseCase by lazy { GetItemUseCase(repository) }

    @Test
    fun testGetItemUseCase_getItem_Completed() = runTest  {
        whenever(  repository.getDataItem(any())).thenReturn(
            flow {
                emit( dataItem )
            }
        )

        val item =  getItemUseCase.execute("").first()
        assertThat(item).isEqualTo(dataItem)
    }


    private companion object {
        private const val itemId = "fake_id"
        private val dataItem  =  DataItem(itemId, "", "", "", "", 0, 0)
    }


}