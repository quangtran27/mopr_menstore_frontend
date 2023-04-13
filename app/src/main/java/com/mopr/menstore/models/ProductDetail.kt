package com.mopr.menstore.models

data class ProductDetail(
	val id: Int,
	val product: Int,
	val sold: Int,
	val quantity: Int,
	val onSale: Boolean,
	val price: Int,
	val salePrice: Int,
	val size: String,
	val color: String,
)
