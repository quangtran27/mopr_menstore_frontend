package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.ItemOrderManageBinding
import com.mopr.menstore.models.OrderItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class OrderItemManageAdapter (
    private val context: Context,
    private val orderItems: List<OrderItem>,
    private val products: List<Product>,
    private val productDetails: List<ProductDetail>,
    private val firstProductImages: List<ProductImage>
): RecyclerView.Adapter<OrderItemManageAdapter.OrderItemManageViewHolder>() {
    inner class OrderItemManageViewHolder(private val binding: ItemOrderManageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind (
            orderItem: OrderItem,
            product: Product,
            productDetail: ProductDetail,
            productImage: ProductImage
        ) {
            Glide.with(context).load(Constants.BASE_IMAGE_URL + productImage.image)
                .into(binding.ivImageItem)
            binding.tvNameItem.text = product.name
            binding.tvClassifyItem.text = productDetail.color + ", " + productDetail.size
            binding.tvQuantityItem.text = "x${orderItem.quantity}"
            binding.tvPriceItem.text = Formatter.formatVNDAmount(orderItem.price.toLong())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemManageViewHolder {
        val binding = ItemOrderManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemManageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    override fun onBindViewHolder(holder: OrderItemManageViewHolder, position: Int) {
        holder.bind(orderItems[position], products[position], productDetails[position], firstProductImages[position])
    }
}