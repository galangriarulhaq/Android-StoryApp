package com.bangkit.storyapp.di

import android.content.Context
import com.bangkit.storyapp.data.local.room.StoryDatabase
import com.bangkit.storyapp.data.remote.retrofit.ApiConfig
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository

object Injection {
    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }

    fun storyRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, database)
    }


}