package com.muralex.achiever.presentation.activities.search_images

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.muralex.achiever.presentation.activities.search_images.image_api.ImageResponse
import com.muralex.achiever.presentation.activities.search_images.image_api.RetrofitAPI
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SearchImageViewModel @Inject constructor(
    private val retrofitApi : RetrofitAPI
        ) : ViewModel() {

    var maxListSize = INIT_MAX_SIZE

    private fun resetListMaxSize() {
        maxListSize = INIT_MAX_SIZE
    }

    private val images = MutableLiveData<Resource<ImageResponse>>()

    val imageList : LiveData<Resource<ImageResponse>>
        get() = images

    @SuppressLint("NullSafeMutableLiveData")
    fun searchForImage(searchString: String) {

        if (searchString.isEmpty()) {
            return
        }

        images.value = Resource.loading(null)

        viewModelScope.launch {
            val response  = searchImage(searchString)
            response?.let {
                images.value = response
            }
        }

    }

    private suspend fun searchImage(searchString: String): Resource<ImageResponse>? {

        return try {

            val response = retrofitApi.imageSearch(searchString)

            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Error", null)
            } else {
                Resource.error("Error", null)
            }

        } catch (e: Exception) {
            Resource.error("No data!", null)
        }
    }


    companion object {
        const val INIT_MAX_SIZE = 5
        const val PAGE_SIZE = 5
    }

}