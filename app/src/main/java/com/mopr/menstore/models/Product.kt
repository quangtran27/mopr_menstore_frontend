package com.mopr.menstore.models

data class Product(
	val id: Int,
	val name: String,
	val category: Int,
	val desc: String,
	val status: Boolean,
	val details: List<ProductDetail>,
	val images: List<ProductImage>, // Constants.BASE_URL + <object>.image
)
