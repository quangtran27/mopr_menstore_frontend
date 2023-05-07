package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.R
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.CompactProductAdapter
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.CategoryApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentHomeBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.CategoryApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
	private lateinit var binding: FragmentHomeBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentHomeBinding.inflate(layoutInflater)

		binding.header.ibCart.setOnClickListener {
//			startActivity(Intent(requireContext(), CartActivity))
		}

		fetchData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		return binding.root
	}

	companion object {
		/**
		 * @return A new instance of fragment HomeFragment.
		 */
		// TODO: Rename and change types and number of parameters
		@JvmStatic
		fun newInstance() =
			HomeFragment().apply {
				arguments = Bundle().apply {
				}
			}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun fetchData() {
		val productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		val categoryApiUtil = CategoryApiUtil(RetrofitClient.getRetrofit().create(CategoryApiService::class.java))

		lifecycleScope.launch {
			try {
				val categories = categoryApiUtil.getAllCategories()
				bindCategories(categories)

				val topSaleProducts = productApiUtil.getTopSaleProducts()
				val topSaleProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
				val topSaleProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()

				for (product in topSaleProducts) {
					topSaleProductDetailsList.add(productApiUtil.getProductDetails(product.id))
					topSaleProductImagesList.add(productApiUtil.getProductImages(product.id))
				}
				bindTopSaleProducts(topSaleProducts, topSaleProductDetailsList, topSaleProductImagesList)

				val latestProducts = productApiUtil.getLatestProducts()
				val latestProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
				val latestProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()

				for (product in latestProducts) {
					latestProductDetailsList.add(productApiUtil.getProductDetails(product.id))
					latestProductImagesList.add(productApiUtil.getProductImages(product.id))
				}

				bindLatestProducts(latestProducts, latestProductDetailsList, latestProductImagesList)
			} catch (e: ApiException) {
				Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindTopSaleProducts(products: List<Product>, topSaleProductDetailsList: List<List<ProductDetail>>, topSaleProductImagesList: List<List<ProductImage?>>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(requireContext(), products, topSaleProductDetailsList, topSaleProductImagesList)
			val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvTopSaleProducts.setHasFixedSize(true)
			binding.rvTopSaleProducts.layoutManager = layoutManager
			binding.rvTopSaleProducts.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindLatestProducts(products: List<Product>, latestProductDetailsList: List<List<ProductDetail>>, latestProductImagesList: List<List<ProductImage?>>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(requireContext(), products, latestProductDetailsList, latestProductImagesList)
			val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvLatestProduct.setHasFixedSize(true)
			binding.rvLatestProduct.layoutManager = layoutManager
			binding.rvLatestProduct.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindCategories(categories: List<Category>) {
		if (categories.isNotEmpty()) {
			val categoryAdapter = CategoryAdapter(requireContext(), categories)
			val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvCategories.setHasFixedSize(true)
			binding.rvCategories.layoutManager = layoutManager
			binding.rvCategories.adapter = categoryAdapter

			categoryAdapter.notifyDataSetChanged()
		}
	}

}