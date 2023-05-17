package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem (
    val id: Int,
    val cartId: Int,
    val productDetailId: Int,
    var quantity: Int,
) : Parcelable