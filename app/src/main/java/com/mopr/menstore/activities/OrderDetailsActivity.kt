package com.mopr.menstore.activities

import SharePrefManager
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
import com.mopr.menstore.fragments.main.NotificationFragment
import com.mopr.menstore.models.Order
import com.mopr.menstore.models.OrderItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Formatter
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class OrderDetailsActivity : AppCompatActivity() {
    private var orderId: Int = -1
    private var status: Int = -1
    private var isPaid: Boolean = false
    private var isReviewed: Boolean = false
    private var order: Order? = null
    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var sharePrefManager: SharePrefManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(this)
        setContentView(binding.root)
        binding.header.ibCart.setOnClickListener{
            if (sharePrefManager.isLoggedIn()){
                val intent = Intent(this@OrderDetailsActivity, AuthenticationActivity::class.java)
                startActivity(intent)
            } else{
                val intent = Intent(this@OrderDetailsActivity, OrderDetailsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.header.tvTitle.text = "Chi tiết đơn hàng"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnReviewOrCancel.setOnClickListener {
            if (order != null) {
                when (order!!.status) {
                    1, 2 -> showDialogCancelOrder(orderId, order!!.isPaid, order!!.isReviewed)
                    4 -> {
                        val intent = Intent(this@OrderDetailsActivity, ReviewActivity::class.java)
                        intent.putExtra("userId", order!!.userId)
                        intent.putExtra("orderId", orderId)
                        this@OrderDetailsActivity.startActivity(intent)
                    }
                }
            }
        }

        orderId = intent.getIntExtra("orderId", -1)
        orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
        Log.d(TAG, "onCreate: $orderId")
        fetchData(orderId)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun fetchData(orderId: Int) {
        lifecycleScope.launch {
            order = orderApiUtil.getOrder(orderId)
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

            Log.d(TAG, "fetchData: $orderItems")
            Log.d(TAG, "fetchData: $products")
            Log.d(TAG, "fetchData: $productDetails")
            Log.d(TAG, "fetchData: $productImages")
            Log.d(TAG, "fetchData: ${order!!.shippingFee}")
            Log.d(TAG, "fetchData: ${order!!.total}")

            bindOrderItems(orderItems, products, productDetails, productImages, order!!.shippingFee, order!!.total)

            binding.tvNameCustomer.text = order!!.name
            binding.tvPhoneCustomer.text = order!!.phone
            binding.tvAddressCustomer.text = order!!.address

            when (order!!.status) {
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
                    binding.btnReviewOrCancel.isEnabled = !order!!.isReviewed
                }
                5 -> {
                    binding.btnReviewOrCancel.text = "Đánh giá"
                    binding.btnReviewOrCancel.isEnabled = false
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
            binding.tvShipPrice.text = Formatter.formatVNDAmount(shippingFee.toLong())
            binding.tvTotalPrice.text = Formatter.formatVNDAmount(total.toLong())
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