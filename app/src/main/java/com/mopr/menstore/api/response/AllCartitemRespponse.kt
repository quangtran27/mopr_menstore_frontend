package com.mopr.menstore.api.response

import com.mopr.menstore.models.CartItem

data class AllCartitemRespponse (
    //val Success : Boolean,
    val data : List<CartItem>,
)

