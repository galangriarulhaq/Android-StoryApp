package com.bangkit.storyapp.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.di.Injection
import com.bangkit.storyapp.ui.model.AddStoryViewModel

class AddStoryViewModelFactory (private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AddStoryViewModelFactory? = null

        fun getInstance(context: Context): AddStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddStoryViewModelFactory(
                    Injection.storyRepository(context),
                )
            }.also { instance = it }
    }

}