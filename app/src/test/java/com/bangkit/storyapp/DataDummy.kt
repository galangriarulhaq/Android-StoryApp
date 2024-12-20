package com.bangkit.storyapp

import com.bangkit.storyapp.data.local.room.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val stories = StoryEntity(
                "2024-05-08T06:$i:18.598Z",
                "Description $i",
                "$i",
                "Name $i",
                "https://story-api.dicoding.dev/images/stories/photos-$i-.png",
                "-0.93$i".toDouble(),
                "100.35$i".toDouble(),
            )
            items.add(stories)
        }
        return items
    }
}