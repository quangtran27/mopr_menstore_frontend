package com.mopr.menstore.api.response

import com.mopr.menstore.models.Category

data class AllCategories(
	val success: Boolean,
	val data: List<Category>
)
