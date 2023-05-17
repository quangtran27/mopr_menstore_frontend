package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ItemCartItemBinding
import com.mopr.menstore.models.CartItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter


class CartItemAdapter(val context: Context, private val cartItems: MutableList<CartItem>, val products: MutableList<Product>, val productDetails: MutableList<ProductDetail>, private val productImagesList: MutableList<List<ProductImage>>, private val selectedCartItems: MutableList<CartItem>) : RecyclerView.Adapter<CartItemAdapter.CartItemVH>() {
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemVH {
        val binding = ItemCartItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartItemVH(binding)
    }

    override fun onBindViewHolder(holder: CartItemVH, position: Int) {
        holder.bind(cartItems[position], products[position], productDetails[position], productImagesList[position])
    }

    override fun getItemCount() = cartItems.size

    inner class CartItemVH(private val binding: ItemCartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartItem: CartItem, product: Product, productDetail: ProductDetail, productImages: List<ProductImage>) {
            binding.tvProductName.text = product.name
            binding.tvProductDetail.text = "Phân loại: ${productDetail.size} - ${productDetail.color}"
            binding.etQuantity.setText(cartItem.quantity.toString())
            if (productImages.isNotEmpty()) {
                Glide.with(context).load(Constants.BASE_IMAGE_URL + productImages[0].image).into(binding.ivProductImage)
            }
            if (productDetail.onSale) {
                binding.tvProductOldPrice.visibility = View.VISIBLE
                binding.tvProductPrice.text = Formatter.formatVNDAmount(productDetail.salePrice.toLong())
                binding.tvProductOldPrice.text = Formatter.formatVNDAmount(productDetail.price.toLong())
                binding.tvProductOldPrice.paintFlags =
                    binding.tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvProductOldPrice.visibility = View.GONE
                binding.tvProductPrice.text = Formatter.formatVNDAmount(productDetail.price.toLong())
            }

            binding.cbSelected.isChecked = selectedCartItems.any { it.id == cartItem.id }
            binding.ibIncrease.isEnabled = cartItem.quantity < productDetail.quantity
            binding.ibDecrease.isEnabled = cartItem.quantity > 1
            if (!binding.ibIncrease.isEnabled) {
                binding.ibIncrease.setImageResource(R.drawable.ic_plus_gray)
            }
            if (!binding.ibDecrease.isEnabled) {
                binding.ibDecrease.setImageResource(R.drawable.ic_minus_gray)
            }

            binding.cbSelected.setOnCheckedChangeListener { _, isChecked ->
                listener?.onSelected(adapterPosition, isChecked)
            }
            binding.ibDelete.setOnClickListener {
                listener?.onDeleteButtonClick(adapterPosition)
            }
            binding.ibIncrease.setOnClickListener {
                listener?.onChangeQuantity(adapterPosition, cartItem.quantity + 1)
            }
            binding.ibDecrease.setOnClickListener {
                listener?.onChangeQuantity(adapterPosition, cartItem.quantity - 1)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteButtonClick(position: Int)
        fun onChangeQuantity(position: Int, quantity: Int)
        fun onSelected(position: Int, isChecked: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}