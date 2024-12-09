package com.bangkit.storyapp.data.remote.retrofit

import com.bangkit.storyapp.data.remote.response.DetailStoryResponse
import com.bangkit.storyapp.data.remote.response.LoginResponse
import com.bangkit.storyapp.data.remote.response.RegisterResponse
import com.bangkit.storyapp.data.remote.response.StoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse

}