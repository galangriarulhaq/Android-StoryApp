package com.bangkit.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.data.local.room.StoryEntity
import com.bangkit.storyapp.databinding.ItemStoryBinding
import com.bangkit.storyapp.ui.DetailActivity
import com.bangkit.storyapp.util.formatDate
import com.bumptech.glide.Glide

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    class StoryViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.imgStory)

            binding.textName.text = story.name
            binding.textDate.text = formatDate(story.createdAt)
            binding.textDescription.text = story.description

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("STORY_ID", story.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}