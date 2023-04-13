package com.mopr.menstore.models

data class CartItem(
	val id: Int,
	val productDetail: ProductDetail,
	val quantity: Int,
)