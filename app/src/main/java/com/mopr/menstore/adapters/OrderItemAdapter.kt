package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.ItemOrderBinding
import com.mopr.menstore.models.OrderItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants

class OrderItemAdapter(
    private val context: Context,
    private val orderItems: List<OrderItem>,
    private val products: List<Product>,
    private val productDetails: List<ProductDetail>,
    private val firstProductImages: List<ProductImage>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemAdapter.OrderItemViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemAdapter.OrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        val product = products[position]
        val productDetail = productDetails[position]
        val image = firstProductImages[position]
        holder.bind(orderItem, product, productDetail, image)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    inner class OrderItemViewHolder(private val binding: ItemOrderBinding) :
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
            binding.tvPriceItem.text = orderItem.price.toString()
        }
    }
}