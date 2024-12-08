package com.bangkit.storyapp.data.repository

import com.bangkit.storyapp.data.Result
import com.bangkit.storyapp.data.remote.response.StoryResponse
import com.bangkit.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun getAllStories(
        token: String
    ): Result<StoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllStories("Bearer $token")

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
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }

}