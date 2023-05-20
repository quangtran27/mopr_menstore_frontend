package com.mopr.menstore.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.activities.OrderDetailManageActivity
import com.mopr.menstore.activities.OrderDetailsActivity
import com.mopr.menstore.activities.OrdersManageActivity
import com.mopr.menstore.databinding.OrderManageBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class OrderManageAdapter(
    private val context: Fragment,
    private val orders: List<Order>,
    private val listOrderItems: List<List<OrderItem>>,
    private val firstOrderProducts: List<Product>,
    private val firstOrderProductDetails: List<ProductDetail>,
    private val firstProductImages: List<ProductImage>,
    private val status: Int,
) : RecyclerView.Adapter<OrderManageAdapter.OrderManageViewHolder>() {

    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderManageViewHolder {
        val binding = OrderManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderManageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderManageViewHolder, position: Int) {
        holder.bind(
            orders[position],
            listOrderItems[position],
            firstOrderProducts[position],
            firstOrderProductDetails[position],
            firstProductImages[position],
            position
        )
    }

    inner class OrderManageViewHolder(private val binding: OrderManageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            order: Order,
            orderItems: List<OrderItem>,
            product: Product,
            productDetail: ProductDetail,
            productImage: ProductImage,
            position: Int
        ) {
            Glide.with(context).load(Constants.BASE_IMAGE_URL + productImage.image)
                .into(binding.ivImagePro)
            binding.tvNamePro.text = product.name
            binding.tvClassifyPro.text = "${productDetail.color}, ${productDetail.size}"
            binding.tvQuantityPro.text = "x${orderItems[0].quantity}"
            var quantityItems = 0
            for (item in orderItems) {
                quantityItems += item.quantity
            }
            binding.tvQuantityItems.text = "$quantityItems sản phẩm"
            binding.tvPricePro.text = Formatter.formatVNDAmount(orderItems[0].price.toLong())
            binding.tvPriceTotal.text = Formatter.formatVNDAmount(order.total.toLong())
            if (status == 1) {
                binding.btnAction.text = "Chuẩn bị"
                binding.btnAction.isEnabled = true
            }
            if (status == 2) {
                binding.btnAction.text = "Vận chuyển"
                binding.btnAction.isEnabled = true
            }
            if (status == 3) {
                binding.btnAction.text = "Đã giao"
                binding.btnAction.isEnabled = true
            }
            if (status == 4) {
                binding.btnAction.text = "Đã giao"
                binding.btnAction.isEnabled = false
            }
            if (status == 5) {
                binding.btnAction.text = "Xác nhận"
                binding.btnAction.isEnabled = false
            }
            binding.btnAction.setOnClickListener {
                listener?.onAction(order.id, position)
            }
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, OrderDetailManageActivity::class.java)
                intent.putExtra("orderId", order.id)
                itemView.context.startActivity(intent)
            }
        }
    }
    interface OnItemClickListener {
        fun onAction (orderId: Int, position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
