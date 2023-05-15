package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.OrderBinding
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.activities.OrderDetailsActivity
import com.mopr.menstore.activities.ReviewActivity
import com.mopr.menstore.models.*

class OrderAdapter(
    private val context: Fragment,
    private val orders: List<Order>,
    private val listOrderItems: List<List<OrderItem>>,
    private val firstOrderProducts: List<Product>,
    private val firstOrderProductDetails: List<ProductDetail>,
    private val firstProductImages: List<ProductImage>,
    private val status: Int,
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderAdapter.OrderViewHolder {
        val binding = OrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapter.OrderViewHolder, position: Int) {
        val order = orders[position]
        val orderItems = listOrderItems[position]
        val firstProduct = firstOrderProducts[position]
        val firstProductDetail = firstOrderProductDetails[position]
        val firstImageProduct = firstProductImages[position]
        holder.bind(order, orderItems, firstProduct, firstProductDetail, firstImageProduct)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class OrderViewHolder(private val binding: OrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            order: Order,
            orderItems: List<OrderItem>,
            product: Product,
            productDetail: ProductDetail,
            productImage: ProductImage
        ) {
            Glide.with(context).load(Constants.BASE_URL1 + productImage.image)
                .into(binding.ivImagePro)

            binding.tvNamePro.text = product.name
            binding.tvClassifyPro.text = "${productDetail.color}, ${productDetail.size}"
            binding.tvQuantityPro.text = "x${orderItems[0].quantity}"
            var quantityItems = 0
            for (item in orderItems) {
                quantityItems += item.quantity
            }
            binding.tvQuantityItems.text = "$quantityItems sản phẩm"
            binding.tvPricePro.text = orderItems[0].price.toString() + "đ"
            binding.tvPriceTotal.text = order.total.toString() + "đ"
            if (status == 1 || status == 2) {
                binding.btnCancelOrReview.text = "Hủy"
                binding.btnCancelOrReview.isEnabled = true
            }
            if (status == 3) {
                binding.btnCancelOrReview.text = "Hủy"
                binding.btnCancelOrReview.isEnabled = false
            }
            if (status == 4) {
                binding.btnCancelOrReview.text = "Đánh giá"
                if (!order.isReviewed) {
                    binding.btnCancelOrReview.isEnabled = true
                } else {
                    binding.btnCancelOrReview.isEnabled = false
                    binding.tvNoteOrder.text = "Đã đánh giá"
                }
            }
            if (status == 5) {
                binding.btnCancelOrReview.text = "Đánh giá"
                binding.btnCancelOrReview.isEnabled = false
            }
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", order.id)
                intent.putExtra("userId", order.userId)
                intent.putExtra("status", order.status)
                itemView.context.startActivity(intent)
            }
            binding.btnCancelOrReview.setOnClickListener {
                if (order.status == 1 || order.status == 2) {
                    listener!!.onCancelClick(order.id, order.isPaid, order.isReviewed)
                }
                if (order.status == 4) {
                    val intent = Intent(itemView.context, ReviewActivity::class.java)
                    intent.putExtra("orderId", order.id)
                    intent.putExtra("userId", order.userId)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onCancelClick(orderId: Int, isPaid: Boolean, isReviewed: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}