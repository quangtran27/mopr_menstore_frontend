package com.mopr.menstore.fragments.orders_manage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.adapters.OrderManageAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentCompletedManageBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"


class CompletedManageFragment : Fragment() {
    private var param1: String? = null

    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil

    private lateinit var binding: FragmentCompletedManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCompletedManageBinding.inflate(layoutInflater)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchData()
        }
        orderApiUtil =
            OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil =
            ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))

        fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val ordersByUser = orderApiUtil.getAll()
                val sortOrderByUser = ordersByUser.reversed()

                val ordersCompleted: MutableList<Order> = mutableListOf()
                for (item in sortOrderByUser) if (item.status == 4) ordersCompleted.add(item)
                val firstOrderProducts: MutableList<Product> = mutableListOf()
                val listOrderItems: MutableList<List<OrderItem>> = mutableListOf()
                val firstOrderProductDetails: MutableList<ProductDetail> = mutableListOf()
                val firstProductImages: MutableList<ProductImage> = mutableListOf()
                for (item in ordersCompleted) {
                    val orderItems = orderApiUtil.getOrderItems(item.id)
                    val productDetail = productApiUtil.getDetail(orderItems[0].productDetailId)
                    val product = productApiUtil.get(productDetail!!.productId)
                    val image = productApiUtil.getImages(productDetail.productId)
                    listOrderItems.add(orderItems)
                    firstOrderProducts.add(product!!)
                    firstOrderProductDetails.add(productDetail)
                    firstProductImages.add(image[0])
                }

                bindOrdersCompletedByUser(
                    ordersCompleted,
                    listOrderItems,
                    firstOrderProducts,
                    firstOrderProductDetails,
                    firstProductImages
                )

                binding.swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                Log.d(TAG, "fetchData: ${e.message.toString()}")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindOrdersCompletedByUser(
        orders: List<Order>,
        listOrderItems: List<List<OrderItem>>,
        firstOrderProducts: List<Product>,
        firstOrderProductDetails: List<ProductDetail>,
        firstProductImages: List<ProductImage>
    ) {
        if (orders.isNotEmpty() && firstOrderProducts.isNotEmpty()) {
            val orderManageAdapter = OrderManageAdapter(
                this@CompletedManageFragment,
                orders,
                listOrderItems,
                firstOrderProducts,
                firstOrderProductDetails,
                firstProductImages,
                4
            )
            orderManageAdapter.setOnItemClickListener(object :
                OrderManageAdapter.OnItemClickListener {
                override fun onAction(orderId: Int, position: Int) {
                    TODO("Not yet implemented")
                }
            })
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            binding.rcOrders.setHasFixedSize(true)
            binding.rcOrders.adapter = orderManageAdapter
            binding.rcOrders.layoutManager = layoutManager

            orderManageAdapter.notifyDataSetChanged()

            binding.tvOrders.visibility = View.GONE
            binding.svOrders.visibility = View.VISIBLE

        } else {
            binding.svOrders.visibility = View.GONE
            binding.tvOrders.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        fetchData()
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            CompletedManageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }

        const val TAG = "CompletedManageFragment"
    }
}