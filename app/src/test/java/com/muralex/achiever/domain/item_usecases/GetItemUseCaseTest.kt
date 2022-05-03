package com.muralex.achiever.domain.item_usecases

import com.example.noter.domain.usecases.GetItemUseCase
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.domain.DataRepository
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetItemUseCaseTest : BaseUnitTest() {

    private val testDataItem  =  TestDoubles.testDataItem
    private val repository = mock<DataRepository>()
    private val getItemUseCase by lazy { GetItemUseCase(repository) }

    @Test
    fun testGetItemUseCase_getItem_Completed() = runTest  {
        whenever(  repository.getDataItem(any())).thenReturn(
            flow {
                emit( testDataItem )
            }
        )

        val item =  getItemUseCase.execute("").first()
        assertThat(item).isEqualTo(testDataItem)
    }


}