package com.mopr.menstore.models

data class ProductImage(
	val order: Int,
	val url: String, // Constants.BASE_URL + <object>.url
	val desc: String,
)
