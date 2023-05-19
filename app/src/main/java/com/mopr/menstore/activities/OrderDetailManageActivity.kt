package com.mopr.menstore.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.adapters.OrderItemAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.ActivityOrderDetailManageBinding
import com.mopr.menstore.models.OrderItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Formatter
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class OrderDetailManageActivity : AppCompatActivity() {
    private var orderId: Int = -1
    private var status: Int = -1
    private var isPaid: Boolean = false
    private var isReviewed: Boolean = false

    private lateinit var binding: ActivityOrderDetailManageBinding

    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Chi tiết đơn hàng"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnAction.setOnClickListener {
            val isPaidOrder = if (isPaid) 1 else 0
            val isReviewedOrder = if (isReviewed) 1 else 0
            confirmOrder(orderId, status + 1 , isPaidOrder, isReviewedOrder)
        }

        orderId = intent.getIntExtra("orderId", 1)

        orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))

        fetchData(orderId)
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun fetchData(orderId: Int) {
        lifecycleScope.launch {
            val order = orderApiUtil.getOrder(orderId)
            val orderItems = orderApiUtil.getOrderItems(orderId)
            status = order!!.status
            isPaid = order.isPaid
            isReviewed = order.isReviewed
            val products: MutableList<Product> = mutableListOf()
            val productDetails: MutableList<ProductDetail> = mutableListOf()
            val productImages: MutableList<ProductImage> = mutableListOf()
            for (item in orderItems) {
                val productDetail = productApiUtil.getDetail(item.productDetailId)
                val product = productApiUtil.get(productDetail!!.productId)
                val image = productApiUtil.getImages(product!!.id)
                products.add(product)
                productDetails.add(productDetail)
                productImages.add(image[0])
            }

            bindOrderItems(orderItems, products, productDetails, productImages,order.shippingFee, order.total)

            binding.tvNameCustomer.text = order.name
            binding.tvPhoneCustomer.text = order.phone
            binding.tvAddressCustomer.text = order.address

            when (status) {
                1 -> {
                    binding.btnAction.text = "Chuẩn bị"
                    binding.btnAction.isEnabled = true
                }
                2 -> {
                    binding.btnAction.text = "Vận chuyển"
                    binding.btnAction.isEnabled = true
                }
                3 -> {
                    binding.btnAction.text = "Đã giao"
                    binding.btnAction.isEnabled = true
                }
                4 -> {
                    binding.btnAction.text = "Đã giao"
                    binding.btnAction.isEnabled = false
                }
                5 -> {
                    binding.btnAction.text = "Chuẩn bị"
                    binding.btnAction.isEnabled = false
                }
            }


        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrderItems (
        orderItems: List<OrderItem>,
        products: List<Product>,
        productDetails: List<ProductDetail>,
        firstProductImages: List<ProductImage>,
        shippingFee: Int,
        total: Int
    ) {
        if (orderItems.isNotEmpty() && products.isNotEmpty()) {
            val orderItemsAdapter = OrderItemAdapter (
                this@OrderDetailManageActivity,
                orderItems,
                products,
                productDetails,
                firstProductImages
            )
            val layoutManager =
                LinearLayoutManager(this@OrderDetailManageActivity, LinearLayoutManager.VERTICAL, false)
            binding.rcProducts.setHasFixedSize(true)
            binding.rcProducts.adapter = orderItemsAdapter
            binding.rcProducts.layoutManager = layoutManager
            orderItemsAdapter.notifyDataSetChanged()
            binding.tvShipPrice.text = Formatter.formatVNDAmount(shippingFee.toLong())
            binding.tvTotalPrice.text = Formatter.formatVNDAmount(total.toLong())
        }
    }
    private fun confirmOrder(orderId: Int, status: Int, isPaid: Int, isReviewed: Int) {
        lifecycleScope.launch {
            orderApiUtil.updateOrder(orderId, status, isPaid, isReviewed)
            fetchData(orderId)
        }
    }
}