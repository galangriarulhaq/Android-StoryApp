package com.bangkit.storyapp.ui.model

import com.bangkit.storyapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.remote.response.DetailStoryResponse
import com.bangkit.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailViewModel (private val storyRepository: StoryRepository) : ViewModel()  {

    private val _story = MutableLiveData<DetailStoryResponse>()
    val story: LiveData<DetailStoryResponse> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStory(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.getStory(token, id)
            if (result is Result.Success) {
                _isLoading.value = false
                _story.value = result.data
            } else {
                _isLoading.value = false
                Result.Error("Failed to fetch story")
            }
        }
    }

}