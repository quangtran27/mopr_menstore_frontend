package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.activities.CartActivity
import com.mopr.menstore.activities.SearchActivity
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
	private var categories: List<Category> = emptyList()
	private var topSaleProducts: List<Product> = emptyList()
	private var latestProducts: List<Product> = emptyList()
	private var topSaleProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var topSaleProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()
	private var latestProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var latestProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentHomeBinding.inflate(layoutInflater)

		binding.header.etSearch.isFocusable = false
		binding.header.etSearch.setOnClickListener {
			startActivity(Intent(requireContext(), SearchActivity::class.java))
		}

		binding.header.ibCart.setOnClickListener {
			activity?.let{
				val intent = Intent (it, CartActivity::class.java)
				it.startActivity(intent)
			}
		}

		lifecycleScope.launch {
			fetchData()
			bindData()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		return binding.root
	}

	private suspend fun fetchData() {
		val productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		val categoryApiUtil = CategoryApiUtil(RetrofitClient.getRetrofit().create(CategoryApiService::class.java))

		try {
			categories = categoryApiUtil.getAllCategories()
			topSaleProducts = productApiUtil.getTopSale()
			for (product in topSaleProducts) {
				topSaleProductDetailsList.add(productApiUtil.getDetails(product.id))
				topSaleProductImagesList.add(productApiUtil.getImages(product.id))
			}
			latestProducts = productApiUtil.getLatest()
			for (product in latestProducts) {
				latestProductDetailsList.add(productApiUtil.getDetails(product.id))
				latestProductImagesList.add(productApiUtil.getImages(product.id))
			}
		} catch (e: ApiException) {
			Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
		}
	}

	private fun bindData() {
		bindCategories(categories)
		bindTopSaleProducts(topSaleProducts, topSaleProductDetailsList, topSaleProductImagesList)
		bindLatestProducts(latestProducts, latestProductDetailsList, latestProductImagesList)
	}

	@SuppressLint("NotifyDataSetChanged")
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

	@SuppressLint("NotifyDataSetChanged")
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

	@SuppressLint("NotifyDataSetChanged")
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

		const val TAG = "HomeFragment"
	}
}