package com.bangkit.storyapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.databinding.ActivityDetailBinding
import com.bangkit.storyapp.ui.factory.DetailViewModelFactory
import com.bangkit.storyapp.ui.model.DetailViewModel
import com.bangkit.storyapp.util.formatDate
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel: DetailViewModel by viewModels {
        DetailViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = UserPreferences(this).getToken()
        val storyId = intent.getStringExtra("STORY_ID")

        if (token != null && storyId != null) {
            detailViewModel.getStory(token, storyId)
        } else {
            Toast.makeText(this, "Invalid StoryId or Token", Toast.LENGTH_SHORT).show()
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        detailViewModel.story.observe(this) { detailStory ->
            if (detailStory != null) {
                Glide.with(binding.root.context)
                    .load(detailStory.story.photoUrl)
                    .into(binding.imgStory)

                binding.tvName.text = detailStory.story.name
                binding.tvDate.text = formatDate(detailStory.story.createdAt.toString())
                binding.tvDescription.text = detailStory.story.description

            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}