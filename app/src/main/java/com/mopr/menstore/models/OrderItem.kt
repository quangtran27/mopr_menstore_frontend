package com.mopr.menstore.models

data class OrderItem(
	val id: Int,
	val order_id: Int,
	val product_detail_id: Int,
	val onSale: Boolean,
	val price: Int,
	val salePrice: Int,
	val quantity: Int,
)
