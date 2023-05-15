package com.mopr.menstore.models

data class Product2(
	val id: Int,
	val categoryId: Int,
	val name: String,
	val desc: String,
	val status: Boolean,
	val details: List<ProductDetail>,
	val images: List<String>
)
