package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewImage(val id: Int, val reviewId: Int, val image: String) : Parcelable

