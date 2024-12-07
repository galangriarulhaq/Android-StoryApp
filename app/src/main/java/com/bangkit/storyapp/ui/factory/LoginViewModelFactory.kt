package com.bangkit.storyapp.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.di.Injection
import com.bangkit.storyapp.ui.model.LoginViewModel

class LoginViewModelFactory(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null

        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    UserPreferences(context),
                    Injection.userRepository(context)
                )
            }.also { instance = it }
    }

}