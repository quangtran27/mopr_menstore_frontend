package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ProductImage(
	val id: Int,
	val productId: Int,
	val order: Int,
	val image: String, // Constants.BASE_URL + <object>.url
	val desc: String,
) : Parcelable
