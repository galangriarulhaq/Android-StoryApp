package com.bangkit.storyapp.ui.model


import com.bangkit.storyapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.data.remote.response.LoginResponse
import com.bangkit.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
): ViewModel() {

    private val _loginResponse = MutableLiveData<Result<LoginResponse>>()
    val loginResponse: LiveData<Result<LoginResponse>> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            _loginResponse.value = result
            if (result is Result.Success) {
                result.data.loginResult?.token?.let { token ->
                    userPreferences.saveToken(token)
                }
            }
            _isLoading.value = false
        }
    }

}