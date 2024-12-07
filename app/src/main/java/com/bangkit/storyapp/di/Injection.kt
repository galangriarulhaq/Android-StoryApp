package com.bangkit.storyapp.di

import android.content.Context
import com.bangkit.storyapp.data.remote.retrofit.ApiConfig
import com.bangkit.storyapp.data.repository.UserRepository

object Injection {
    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }

}