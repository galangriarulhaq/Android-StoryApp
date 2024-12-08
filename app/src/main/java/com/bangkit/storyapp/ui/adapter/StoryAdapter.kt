package com.bangkit.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.data.remote.response.ListStoryItem
import com.bangkit.storyapp.databinding.ItemStoryBinding
import com.bangkit.storyapp.util.formatDate
import com.bumptech.glide.Glide

class StoryAdapter(private val onItemClick: (String) -> Unit): ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    class StoryViewHolder(private val binding: ItemStoryBinding, private val onItemClick: (String) -> Unit): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.imgStory)

            binding.textName.text = story.name
            binding.textDate.text = formatDate(story.createdAt.toString())
            binding.textDescription.text = story.description

            itemView.setOnClickListener {
                onItemClick(story.id.toString())
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}