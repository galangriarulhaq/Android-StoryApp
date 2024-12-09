package com.bangkit.storyapp.ui.model

import com.bangkit.storyapp.data.Result
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.remote.response.PostResponse
import com.bangkit.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _postResponse = MutableLiveData<Result<PostResponse>>()
    val postResponse: LiveData<Result<PostResponse>> = _postResponse

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getImageUri(): Uri? {
        return _currentImageUri.value
    }

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun postStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.postStory(token, description, photo, lat, lon)
            _postResponse.value = result
            _isLoading.value = false
        }
    }

}