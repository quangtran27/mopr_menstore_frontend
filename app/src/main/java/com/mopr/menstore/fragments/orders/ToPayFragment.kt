package com.mopr.menstore.fragments.orders

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.R
import com.mopr.menstore.adapters.OrderAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentToPayBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

private const val USER_ID = "user_id"

class ToPayFragment : Fragment() {
    private var userId: Int = 0

    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil

    private lateinit var binding: FragmentToPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentToPayBinding.inflate(layoutInflater)
        arguments?.let {
            userId = it.getInt(USER_ID)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchData(userId)
        }

        orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))

        fetchData(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(userId: Int){
        lifecycleScope.launch {
            try {
                val ordersByUser = orderApiUtil.getOrdersByUser(userId)
                val sortOrderByUser = ordersByUser.reversed()

                val ordersToPay : MutableList<Order> = mutableListOf()
                for (item in sortOrderByUser) if (item.status == 1) ordersToPay.add(item)
                val firstOrderProducts : MutableList<Product> = mutableListOf()
                val listOrderItems: MutableList<List<OrderItem>> = mutableListOf()
                val firstOrderProductDetails: MutableList<ProductDetail> = mutableListOf()
                val firstProductImages: MutableList<ProductImage> = mutableListOf()
                for (item in ordersToPay) {
                    val orderItems = orderApiUtil.getOrderItems(item.id)
                    val productDetail = productApiUtil.getDetail(orderItems[0].productDetailId)
                    val product = productApiUtil.get(productDetail!!.productId)
                    val image = productApiUtil.getImages(productDetail.productId)
                    listOrderItems.add(orderItems)
                    firstOrderProducts.add(product!!)
                    firstOrderProductDetails.add(productDetail)
                    firstProductImages.add(image[0])
                }

                bindOrdersToPayByUser(ordersToPay,listOrderItems, firstOrderProducts,firstOrderProductDetails,firstProductImages)

                binding.swipeRefreshLayout.isRefreshing = false
            }catch (e: Exception) {
                Log.d(TAG, "fetchData: ${e.message.toString()}")
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrdersToPayByUser( orders: List<Order>, listOrderItems: List<List<OrderItem>>, firstOrderProducts: List<Product>, firstOrderProductDetails: List<ProductDetail>, firstProductImages: List<ProductImage>){
        if(orders.isNotEmpty() && firstOrderProducts.isNotEmpty()){
            val orderAdapter= OrderAdapter(this@ToPayFragment, orders, listOrderItems, firstOrderProducts,firstOrderProductDetails,firstProductImages, 1)
            orderAdapter.setOnItemClickListener(object: OrderAdapter.OnItemClickListener{
                override fun onCancelClick(orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
                    showDialogCancelOrder(orderId, isPaid, isReviewed)
                }
            })
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            binding.rcOrders.setHasFixedSize(true)
            binding.rcOrders.adapter= orderAdapter
            binding.rcOrders.layoutManager= layoutManager

            orderAdapter.notifyDataSetChanged()

            binding.tvOrders.visibility = View.GONE
            binding.svOrders.visibility = View.VISIBLE

        } else {
            binding.svOrders.visibility = View.GONE
            binding.tvOrders.visibility = View.VISIBLE
        }
    }

    private fun showDialogCancelOrder(orderId: Int, isPaid: Boolean, isReviewed: Boolean){
        val confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.confirm_cancel_order_dialog)
        confirmDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        confirmDialog.setCancelable(false)
        val  okButton: Button = confirmDialog.findViewById(R.id.btnOk)
        val cancelButton: Button = confirmDialog.findViewById(R.id.btnCancel)
        okButton.setOnClickListener {
            lifecycleScope.launch {
                cancelOrder(orderId, isPaid, isReviewed)
                confirmDialog.dismiss()
            }
        }
        cancelButton.setOnClickListener{ confirmDialog.dismiss() }
        confirmDialog.show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun cancelOrder (orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
        val isPaidCancel: Int = if (!isPaid) 0 else 1
        val isReviewedCancel: Int = if (!isReviewed) 0 else 1
        lifecycleScope.launch {
            orderApiUtil.updateOrder(orderId, 5, isPaidCancel, isReviewedCancel)
            fetchData(orderId)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(userId: Int) =
            ToPayFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
        const val TAG = "ToPayFragment"
    }
}