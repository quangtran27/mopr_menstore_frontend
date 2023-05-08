package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
	val id: Int,
	val userId: Int,
	val productId: Int,
	val created: String, // yyyy-mm-dddd
	val star: Int,
	val desc: String,
) : Parcelable
