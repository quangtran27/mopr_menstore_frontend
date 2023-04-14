package com.mopr.menstore.api.response

import com.mopr.menstore.models.Product

data class AllProductsResponse(
	val success: Boolean,
	val data: List<Product>,
)
