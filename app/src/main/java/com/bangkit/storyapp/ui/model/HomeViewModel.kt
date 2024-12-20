package com.bangkit.storyapp.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapp.data.local.room.StoryEntity
import com.bangkit.storyapp.data.repository.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun stories(token: String): LiveData<PagingData<StoryEntity>> {
        _isLoading.value = true
        return storyRepository.getAllStories(token)
            .cachedIn(viewModelScope)
            .also {
                _isLoading.value = false
            }
    }
}