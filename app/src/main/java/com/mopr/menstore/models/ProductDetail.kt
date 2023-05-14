package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDetail(
	val id: Int,
	val productId: Int,
	val sold: Int,
	val quantity: Int,
	val onSale: Boolean,
	val price: Int,
	val salePrice: Int,
	val size: String,
	var color: String,
) : Parcelable
