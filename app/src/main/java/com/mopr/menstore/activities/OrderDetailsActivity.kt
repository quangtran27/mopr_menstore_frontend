package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.adapters.OrderItemAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.ActivityOrderDetailsBinding
import com.mopr.menstore.models.OrderItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val orderId = intent.getIntExtra("orderId", 1)
        val userId = intent.getIntExtra("userId", 1)
        val status = intent.getIntExtra("status", 1)
        val isPaid = intent.getBooleanExtra("isPaid", false)
        val isReviewed = intent.getBooleanExtra("isReviewed", false)
        fetchData(orderId)
        binding.btnReviewOrCancel.setOnClickListener {
            when (status) {
                1 -> cancelOrder(orderId, isPaid, isReviewed)
                2 -> cancelOrder(orderId, isPaid, isReviewed)
                4 -> {
                    val intent = Intent(this@OrderDetailsActivity, ReviewActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("orderId", orderId)
                    this@OrderDetailsActivity.startActivity(intent)
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(orderId: Int){
        val orderApiService = RetrofitClient.getRetrofit().create(OrderApiService::class.java)
        val orderApiUtils = OrderApiUtil(orderApiService)
        val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
        val productApiUtil = ProductApiUtil(productApiService)
        lifecycleScope.launch {
            val order = orderApiUtils.getOrder(orderId)
            val orderItems = orderApiUtils.getOrderItems(orderId)
            var products: MutableList<Product> = mutableListOf()
            var productDetails: MutableList<ProductDetail> = mutableListOf()
            var productImages: MutableList<ProductImage> = mutableListOf()
            for (item in orderItems!!){
                val productDetail = productApiUtil.getDetail(item.productDetailId)
                val product = productApiUtil.get(productDetail!!.productId)
                val image = productApiUtil.getImages(product!!.id)
                products.add(product!!)
                productDetails.add(productDetail!!)
                productImages.add(image!![0])
            }
            bindOrderItems(orderItems, products,productDetails, productImages, order!!.total)
            binding.tvNameCustomer.text = order!!.name
            binding.tvPhoneCustomer.text = order!!.phone
            binding.tvAddressCustomer.text = order!!.address
            when (order!!.status){
                1-> {
                    binding.btnReviewOrCancel.text = "Hủy"
                    binding.btnReviewOrCancel.isEnabled= true
                }
                2-> {
                    binding.btnReviewOrCancel.text = "Hủy"
                    binding.btnReviewOrCancel.isEnabled= true
                }
                3-> {
                    binding.btnReviewOrCancel.text = "Hủy"
                    binding.btnReviewOrCancel.isEnabled= false
                }
                4-> {
                    binding.btnReviewOrCancel.text = "Đánh giá"
                    binding.btnReviewOrCancel.isEnabled= true
                }
                5-> {
                    binding.btnReviewOrCancel.text = "Đánh giá"
                    binding.btnReviewOrCancel.isEnabled= false
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrderItems(
        orderItems: List<OrderItem>,
        products: List<Product>,
        productDetails: List<ProductDetail>,
        firstProductImages: List<ProductImage>,
        total: Int){
        if (orderItems.isNotEmpty() && products.isNotEmpty()){
            val orderItemsAdapter = OrderItemAdapter(this@OrderDetailsActivity, orderItems, products, productDetails,firstProductImages)
            val layoutManager = LinearLayoutManager(this@OrderDetailsActivity, LinearLayoutManager.VERTICAL, false)
            binding.rcProducts.setHasFixedSize(true)
            binding.rcProducts.adapter = orderItemsAdapter
            binding.rcProducts.layoutManager = layoutManager
            orderItemsAdapter.notifyDataSetChanged()
            binding.tvTotalPrice.text = total.toString()
        }
    }
    private fun cancelOrder (orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
        val orderApiService = RetrofitClient.getRetrofit().create(OrderApiService::class.java)
        val orderApiUtil = OrderApiUtil(orderApiService)
        val statusCancel = 5
        var isPaidCancel: Int
        if (!isPaid) {
            isPaidCancel = 0
        } else {
            isPaidCancel = 1
        }
        var isReviewedCancel: Int
        if (!isReviewed) {
            isReviewedCancel = 0
        } else {
            isReviewedCancel = 1
        }
        lifecycleScope.launch {
            try {
                orderApiUtil.updateOrder(orderId, statusCancel, isPaidCancel, isReviewedCancel)
                fetchData(1)
            }catch (e: Exception) {
                Log.d("ToPayCancelOrderError", e.message.toString())
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        // Kết thúc Activity hiện tại và quay trở lại Activity trước đó
        startActivity(Intent(applicationContext, OrdersActivity::class.java))
        finish()
        return true
    }
}