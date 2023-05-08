package com.mopr.menstore.models

data class ListResponse<T>(
	val data: List<T>,
	val pagination: Pagination,
)
