package com.muralex.achiever.presentation.di

import android.content.Context
import com.example.noter.domain.usecases.GetItemUseCase
import com.muralex.achiever.data.database.AppDatabase
import com.muralex.achiever.data.database.DataItemDao
import com.muralex.achiever.data.models.mappers.GroupStatsDataMapper
import com.muralex.achiever.data.models.mappers.ItemInGroupMapper
import com.muralex.achiever.data.models.mappers.PinnedItemsMapper
import com.muralex.achiever.data.models.mappers.SearchItemMapper
import com.muralex.achiever.data.repository.DataRepositoryImpl
import com.muralex.achiever.domain.DataRepository
import com.muralex.achiever.domain.group_usecases.GetGroupsListUseCase
import com.muralex.achiever.notifications.NotifyWorker
import com.muralex.achiever.presentation.activities.search_images.image_api.RetrofitAPI
import com.muralex.achiever.presentation.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext appContext: Context) : AppDatabase {
        return AppDatabase.getInstance(appContext)
    }

    @Singleton
    @Provides
    fun providesDao(database: AppDatabase) : DataItemDao {
        return database.dataItemDAO
    }

    @Singleton
    @Provides
    fun provideItemRepository (
        dao: DataItemDao,
        itemInGroupMapper: ItemInGroupMapper,
        groupStatsDataMapper: GroupStatsDataMapper,
        pinnedItemsMapper: PinnedItemsMapper,
        searchItemMapper: SearchItemMapper
    ) : DataRepository {
        return DataRepositoryImpl(
            dao,
            itemInGroupMapper,
            groupStatsDataMapper,
            pinnedItemsMapper,
            searchItemMapper)
    }

    @Singleton
    @Provides
    fun injectRetrofitAPI() : RetrofitAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(RetrofitAPI::class.java)
    }



}