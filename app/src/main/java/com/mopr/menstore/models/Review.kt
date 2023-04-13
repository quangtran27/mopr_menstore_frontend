package com.mopr.menstore.models

data class Review(
	val id: Int,
	val user: Int,
	val created: String, // yyyy-mm-dddd
	val star: Int,
	val desc: String,
	val images: List<String>,
)
