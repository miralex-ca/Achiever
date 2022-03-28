package com.muralex.achiever.presentation.activities.search_images.image_api

import com.muralex.achiever.presentation.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitAPI {
    @GET("/api/")
    suspend fun imageSearch(

        @Query("q") searchQuery: String,
        @Query("key") apiKey : String = API_KEY,
        @Query ("per_page") perPAge : Int = 30

    ) : Response<ImageResponse>
}