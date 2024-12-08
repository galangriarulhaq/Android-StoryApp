package com.bangkit.storyapp.ui.model

import com.bangkit.storyapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.remote.response.ListStoryItem
import com.bangkit.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storiesResponse = MutableLiveData<Result<List<ListStoryItem>>>()
    val storiesResponse: LiveData<Result<List<ListStoryItem>>> = _storiesResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStories(token: String) {
        viewModelScope.launch {
            _storiesResponse.value =
                storyRepository.getAllStories(token).let { result ->
                    if (result is Result.Success) {
                        Result.Success(result.data.listStory)
                    } else {
                        Result.Error("Failed to fetch stories")
                    }
                }
        }
    }
}