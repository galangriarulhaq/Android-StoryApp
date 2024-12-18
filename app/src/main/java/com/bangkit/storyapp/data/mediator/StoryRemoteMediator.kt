package com.bangkit.storyapp.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bangkit.storyapp.data.local.room.RemoteKeys
import com.bangkit.storyapp.data.local.room.StoryDatabase
import com.bangkit.storyapp.data.local.room.StoryEntity
import com.bangkit.storyapp.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {

            val response =
                apiService.getAllStories("Bearer $token", page, state.config.pageSize)

            val endOfPaginationReached = response.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d("RemoteMediator", "Clearing remote keys and stories in the database")
                    database.remoteDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val keys = response.listStory.map { story ->
                    RemoteKeys(
                        storyId = story.id.orEmpty(),
                        prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }

                database.remoteDao().insertAll(keys)

                database.storyDao().insertStory(response.listStory.map {
                    StoryEntity(
                        id = it.id.orEmpty(),
                        createdAt = it.createdAt.orEmpty(),
                        name = it.name.orEmpty(),
                        description = it.description.orEmpty(),
                        photoUrl = it.photoUrl.orEmpty(),
                        lon = it.lon,
                        lat = it.lat
                    )
                })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            Log.e("StoryRemoteMediator", "Error during API call", exception)
            return MediatorResult.Error(exception)
        }

    }


    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteDao().remoteKeysStoryId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
            database.remoteDao().remoteKeysStoryId(story.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
            database.remoteDao().remoteKeysStoryId(story.id)
        }
    }
}