package com.bangkit.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)