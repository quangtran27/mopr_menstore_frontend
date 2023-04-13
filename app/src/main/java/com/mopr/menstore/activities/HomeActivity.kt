package com.mopr.menstore.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.CompactProductAdapter
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.ActivityHomeBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
	private lateinit var binding: ActivityHomeBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityHomeBinding.inflate(layoutInflater)
		setContentView(binding.root)
		fetchData()
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun fetchData() {
		val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
		val productApiUtil = ProductApiUtil(productApiService)

		lifecycleScope.launch {
			try {
				val categories = productApiUtil.getAllCategories()
				bindCategories(categories)

				val topSaleProducts = productApiUtil.getTopSaleProducts()
				bindTopSaleProducts(topSaleProducts)

				val latestProducts = productApiUtil.getLatestProducts()
				bindLatestProducts(latestProducts)

			} catch (e: ApiException) {
				Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindTopSaleProducts(products: List<Product>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(this@HomeActivity, products)
			val layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

			binding.rvTopSaleProducts.setHasFixedSize(true)
			binding.rvTopSaleProducts.layoutManager = layoutManager
			binding.rvTopSaleProducts.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindLatestProducts(products: List<Product>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(this@HomeActivity, products)
			val layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

			binding.rvLatestProduct.setHasFixedSize(true)
			binding.rvLatestProduct.layoutManager = layoutManager
			binding.rvLatestProduct.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindCategories(categories: List<Category>) {
		if (categories.isNotEmpty()) {
			val categoryAdapter = CategoryAdapter(this@HomeActivity, categories)
			val layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

			binding.rvCategories.setHasFixedSize(true)
			binding.rvCategories.layoutManager = layoutManager
			binding.rvCategories.adapter = categoryAdapter

			categoryAdapter.notifyDataSetChanged()
		}
	}
}