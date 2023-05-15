package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.R
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
    private var userId: Int = -1
    private var orderId: Int = -1
    private var status: Int = -1
    private var isPaid: Boolean = false
    private var isReviewed: Boolean = false



    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Chi tiết đơn hàng"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnReviewOrCancel.setOnClickListener {
            when (status) {
                1, 2 -> showDialogCancelOrder(orderId, isPaid, isReviewed)
                4 -> {
                    val intent = Intent(this@OrderDetailsActivity, ReviewActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("orderId", orderId)
                    this@OrderDetailsActivity.startActivity(intent)
                }
            }
        }

        orderId = intent.getIntExtra("orderId", 1)
        userId = intent.getIntExtra("userId", 1)
        status = intent.getIntExtra("status", 1)
        isPaid = intent.getBooleanExtra("isPaid", false)
        isReviewed = intent.getBooleanExtra("isReviewed", false)

        orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))

        fetchData(orderId)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun fetchData(orderId: Int) {
        lifecycleScope.launch {
            val order = orderApiUtil.getOrder(orderId)
            val orderItems = orderApiUtil.getOrderItems(orderId)
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

            bindOrderItems(orderItems, products, productDetails, productImages, order!!.total)

            binding.tvNameCustomer.text = order.name
            binding.tvPhoneCustomer.text = order.phone
            binding.tvAddressCustomer.text = order.address

            when (order.status) {
                1, 2 -> {
                    binding.btnReviewOrCancel.text = "Hủy"
                    binding.btnReviewOrCancel.isEnabled = true
                }
                3 -> {
                    binding.btnReviewOrCancel.text = "Hủy"
                    binding.btnReviewOrCancel.isEnabled = false
                }
                4 -> {
                    binding.btnReviewOrCancel.text = "Đánh giá"
                    binding.btnReviewOrCancel.isEnabled = true
                }
                5 -> {
                    binding.btnReviewOrCancel.text = "Đánh giá"
                    binding.btnReviewOrCancel.isEnabled = false
                }
            }

            binding.btnReviewOrCancel.isEnabled = !order.isReviewed
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrderItems (
        orderItems: List<OrderItem>,
        products: List<Product>,
        productDetails: List<ProductDetail>,
        firstProductImages: List<ProductImage>,
        total: Int
    ) {
        if (orderItems.isNotEmpty() && products.isNotEmpty()) {
            val orderItemsAdapter = OrderItemAdapter (
                this@OrderDetailsActivity,
                orderItems,
                products,
                productDetails,
                firstProductImages
            )
            val layoutManager =
                LinearLayoutManager(this@OrderDetailsActivity, LinearLayoutManager.VERTICAL, false)
            binding.rcProducts.setHasFixedSize(true)
            binding.rcProducts.adapter = orderItemsAdapter
            binding.rcProducts.layoutManager = layoutManager
            orderItemsAdapter.notifyDataSetChanged()
            binding.tvTotalPrice.text = total.toString()
        }
    }

    private fun cancelOrder(orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
        val isPaidCancel: Int = if (!isPaid) 0 else 1
        val isReviewedCancel: Int = if (!isReviewed) 0 else 1
        lifecycleScope.launch {
            orderApiUtil.updateOrder(orderId, 5, isPaidCancel, isReviewedCancel)
            fetchData(orderId)
        }
    }

    private fun showDialogCancelOrder(orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
        val confirmDialog = Dialog(this)
        confirmDialog.setContentView(R.layout.confirm_cancel_order_dialog)
        confirmDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        confirmDialog.setCancelable(false)
        val okButton: Button = confirmDialog.findViewById(R.id.btnOk)
        val cancelButton: Button = confirmDialog.findViewById(R.id.btnCancel)
        okButton.setOnClickListener {
            lifecycleScope.launch {
                cancelOrder(orderId, isPaid, isReviewed)
                confirmDialog.dismiss()
            }
        }
        cancelButton.setOnClickListener { confirmDialog.dismiss() }
        confirmDialog.show()
    }
    override fun onResume() {
        super.onResume()
        fetchData(orderId)
    }
    companion object {
        const val TAG = "OrderDetailActivity"
    }
}