package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
	val name: String,
	val phone: String,
	val password: String,
	val birthday: Date,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
) : Parcelable
