package com.bangkit.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.storyapp.data.Result
import com.bangkit.storyapp.data.local.room.StoryDatabase
import com.bangkit.storyapp.data.local.room.StoryEntity
import com.bangkit.storyapp.data.mediator.StoryRemoteMediator
import com.bangkit.storyapp.data.remote.response.DetailStoryResponse
import com.bangkit.storyapp.data.remote.response.PostResponse
import com.bangkit.storyapp.data.remote.response.StoryResponse
import com.bangkit.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    fun getAllStories(
        token: String
    ): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(
                database = storyDatabase,
                apiService = apiService,
                token = token
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getStory(token: String, id: String): Result<DetailStoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStory("Bearer $token", id)
                if (!response.error!!) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                Result.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun postStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ): Result<PostResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.postStory("Bearer $token", description, photo, lat, lon)

                if (!response.error) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                Result.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun getAllStoriesLocation(
        token: String,
        location: Int = 1
    ): Result<StoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllStoriesLocation("Bearer $token", location)

                if (!response.error!!) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                Result.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }

        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase)
            }.also { instance = it }
    }

}