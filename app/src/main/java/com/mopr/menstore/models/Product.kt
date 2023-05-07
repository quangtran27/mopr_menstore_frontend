package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
	val id: Int,
	val categoryId: Int,
	val name: String,
	val desc: String,
	val status: Boolean,
) : Parcelable
