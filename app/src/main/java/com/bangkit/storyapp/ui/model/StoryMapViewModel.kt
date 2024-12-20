package com.bangkit.storyapp.ui.model

import com.bangkit.storyapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.remote.response.ListStoryItem
import com.bangkit.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryMapViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storiesLocation = MutableLiveData<Result<List<ListStoryItem>>>()
    val storiesLocation: LiveData<Result<List<ListStoryItem>>> = _storiesLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStoriesLocation(
        token: String,
        location: Int = 1
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            _storiesLocation.value =
                storyRepository.getAllStoriesLocation(token, location).let { result ->
                    if (result is Result.Success) {
                        _isLoading.value = false
                        Result.Success(result.data.listStory)
                    } else {
                        _isLoading.value = false
                        Result.Error("Failed to fetch stories")
                    }
                }
        }
    }
}