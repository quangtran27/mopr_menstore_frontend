package com.mopr.menstore.fragments.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.adapters.OrderAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentCompletedBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch


private const val USER_ID = "user_id"

class CompletedFragment : Fragment(), OrderAdapter.OnItemClickListener {
    private var userId: Int = 0
    private lateinit var binding: FragmentCompletedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID)
        }
        binding = FragmentCompletedBinding.inflate(layoutInflater)
        fetchData(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(userId: Int) =
            CompletedFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(userId: Int){
        val orderApiService = RetrofitClient.getRetrofit().create(OrderApiService::class.java)
        val orderApiUtil = OrderApiUtil(orderApiService)
        val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
        val productApiUtil = ProductApiUtil(productApiService)
        lifecycleScope.launch {
            try {
                val ordersByUser = orderApiUtil.getOrdersByUser(userId)
                var sortOrderByUser = ordersByUser.reversed()
                var ordersCompleted : MutableList<Order> = mutableListOf()
                for (item in sortOrderByUser) {
                    if (item.status == 4) {
                        ordersCompleted.add(item)
                    }
                }
                var firstOrderProducts : MutableList<Product> = mutableListOf()
                var listOrderItems: MutableList<List<OrderItem>> = mutableListOf()
                var firstOrderProductDetails: MutableList<ProductDetail> = mutableListOf()
                var firstImageProducts: MutableList<ProductImage> = mutableListOf()
                for (item in ordersCompleted) {
                    val orderItems = orderApiUtil.getOrderItems(item.id)
                    val productDetail = productApiUtil.getDetail(orderItems!![0].productDetailId)
                    val product = productApiUtil.get(productDetail!!.productId)
                    val image = productApiUtil.getImages(productDetail.productId)
                    listOrderItems.add(orderItems!!)
                    firstOrderProducts.add(product!!)
                    firstOrderProductDetails.add(productDetail!!)
                    firstImageProducts.add(image!![0])
                }
                bindOrdersCompletedByUser(ordersCompleted, listOrderItems, firstOrderProducts,firstOrderProductDetails,firstImageProducts)
            }catch (e: Exception) {
                Log.d("CompletedfetchDataError", e.message.toString())
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrdersCompletedByUser(orders: List<Order>, listOrderItems: List<List<OrderItem>>, firstOrderProducts: List<Product>, firstOrderProductDetails: List<ProductDetail>, firstImageProducts: List<ProductImage>){
        if(orders.isNotEmpty() && firstOrderProducts.isNotEmpty()){
            val orderAdapter= OrderAdapter(this@CompletedFragment, orders,listOrderItems, firstOrderProducts,firstOrderProductDetails,firstImageProducts, 4, this@CompletedFragment)
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rcOrders.setHasFixedSize(true)
            binding.rcOrders.adapter= orderAdapter
            binding.rcOrders.layoutManager= layoutManager
            orderAdapter.notifyDataSetChanged()
            binding.tvOrders.visibility = View.GONE
        } else {
            binding.svOrders.visibility = View.GONE
        }
    }

    override fun onCancelClick(orderId: Int, isPaid: Boolean, isReviewed: Boolean) {
        TODO("Not yet implemented")
    }
}