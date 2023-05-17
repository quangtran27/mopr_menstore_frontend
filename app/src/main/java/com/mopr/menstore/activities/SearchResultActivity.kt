package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.adapters.ProductAdapter
import com.mopr.menstore.adapters.StringAdapter
import com.mopr.menstore.api.CategoryApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.ActivitySearchResultBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.CategoryApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch


class SearchResultActivity : AppCompatActivity() {
	private val starOptions = mutableListOf("Từ 0 sao", "Từ 1 sao", "Từ 2 sao", "Từ 3 sao", "Từ 4 sao", "Từ 5 sao")
	private val starOptionsMapping = mutableListOf("0", "1", "2", "3", "4", "5")
	private val sortByOptions = mutableListOf("Mặc định", "Giá bán", "Bán chạy nhất", "Mới nhất")
	private val sortByOptionsMapping = mutableListOf("", "price", "sales", "created")
	private val orderOptions = mutableListOf("Tăng dần", "Giảm dần")
	private val orderOptionsMapping = mutableListOf("", "desc")
	private lateinit var binding: ActivitySearchResultBinding
	private var currentPage = 1
	private var total = 0
	private var products: MutableList<Product> = mutableListOf()
	private var categories: MutableList<Category> = mutableListOf(Category(0, "Tất cả", "", null))
	private var productDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var productImagesList: MutableList<List<ProductImage>> = mutableListOf()
	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var categoryApiUtil: CategoryApiUtil
	private lateinit var categoryAdapter: StringAdapter
	private lateinit var starAdapter: StringAdapter
	private lateinit var orderAdapter: StringAdapter
	private lateinit var sortByAdapter: StringAdapter
	private lateinit var productAdapter: ProductAdapter
	private var isScrolling = false
	private var categoriesName: MutableList<String> = mutableListOf("Tất cả")

	// parameters for filter
	private var keyword = ""
	private var selectedCategory = "Tất cả"
	private var selectedStar = "Từ 0 sao"
	private var selectedSortBy = "Mặc định"
	private var selectedOrder = "Tăng dần"
	private var minPrice = 0.toString()
	private var maxPrice = 999999999.toString()

	@SuppressLint("NotifyDataSetChanged")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivitySearchResultBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Set events
		binding.dlMain.closeDrawer(GravityCompat.END)
		keyword = intent.getStringExtra("keyword") ?: ""
		binding.header.etSearch.setText(keyword)
		binding.header.etSearch.isFocusable = false
		binding.progressBar.visibility = View.GONE
		binding.header.ibBack.setOnClickListener {
			onBackPressedDispatcher.onBackPressed()
		}
		binding.header.etSearch.setOnClickListener {
			val intent = Intent(this, SearchActivity::class.java)
			intent.putExtra("keyword", "")
			startActivity(intent)
		}
		binding.header.btnFilter.setOnClickListener {
			binding.header.btnFilter.setOnClickListener {
				binding.dlMain.openDrawer(GravityCompat.END)
			}
		}
		binding.btnApply.setOnClickListener {
			val minPrice = binding.etMinPrice.text.trim().toString()
			val maxPrice = binding.etMaxPrice.text.trim().toString()

			if (minPrice.toDoubleOrNull() != null && maxPrice.toDoubleOrNull() != null) {
				lifecycleScope.launch {
					currentPage = 1
					products = mutableListOf()
					productDetailsList = mutableListOf()
					productImagesList = mutableListOf()
					fetchProducts()
				}
			} else {
				Toast.makeText(this, "Mức giá không hợp lệ", Toast.LENGTH_SHORT).show()
			}
		}

		// Binding data
		binding.etMinPrice.setText(minPrice)
		binding.etMaxPrice.setText(maxPrice)
		productAdapter = ProductAdapter(
			this@SearchResultActivity,
			products,
			productDetailsList,
			productImagesList
		)

		productApiUtil =
			ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		categoryApiUtil =
			CategoryApiUtil(RetrofitClient.getRetrofit().create(CategoryApiService::class.java))

		lifecycleScope.launch {
			fetchProducts()
			fetchCategories()
			bindProducts()
			setSelectedCategory("Tất cả")
			setSelectedStar("Từ 0 sao")
			setSelectedOrder("Tăng dần")
			setSelectedSortBy("Mặc định")
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private suspend fun fetchProducts() {
		val options: Map<String, String> = mapOf(
			"page" to "$currentPage",
			"name" to keyword,
			"categoryId" to "${categories.find { category -> category.name == selectedCategory }?.id}",
			"star" to starOptionsMapping[starOptions.indexOf(selectedStar)],
			"sortBy" to sortByOptionsMapping[sortByOptions.indexOf(selectedSortBy)],
			"order" to orderOptionsMapping[orderOptions.indexOf(selectedOrder)],
			"minPrice" to binding.etMinPrice.text.trim().toString(),
			"maxPrice" to binding.etMaxPrice.text.trim().toString(),
		)
		val productsResponse = productApiUtil.getAll(options)
		total = productsResponse!!.pagination.total
		for (product in productsResponse.data) {
			products.add(product)
			productDetailsList.add(productApiUtil.getDetails(product.id))
			productImagesList.add(productApiUtil.getImages(product.id))
		}

		val newProductAdapter = ProductAdapter(this@SearchResultActivity, products, productDetailsList, productImagesList)
		binding.rvProducts.adapter = newProductAdapter

		productAdapter.notifyDataSetChanged()
	}

	@SuppressLint("NotifyDataSetChanged")
	private suspend fun fetchCategories() {
		val response = categoryApiUtil.getAllCategories().toMutableList()
		for (category in response) {
			categories.add(category)
			categoriesName.add(category.name)
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindProducts() {
		if (products.isNotEmpty()) {
			binding.tvEmptyProducts.visibility = View.GONE
			binding.rvProducts.apply {
				setHasFixedSize(true)
				addOnScrollListener(object : RecyclerView.OnScrollListener() {
					override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
						if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
							isScrolling = true
						}
					}

					@SuppressLint("NotifyDataSetChanged")
					override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
						val manager: LinearLayoutManager = layoutManager as LinearLayoutManager
						val currentItems = manager.childCount
						val totalItems = manager.itemCount
						val scrollOutItems = manager.findFirstVisibleItemPosition()

						if (isScrolling && (currentItems + scrollOutItems == totalItems) && (currentItems + scrollOutItems < total)) {
							currentPage += 1
							lifecycleScope.launch {
								binding.progressBar.visibility = View.VISIBLE
								Log.d(
									TAG,
									"onScrolled: fetch more with current page: $currentPage"
								)
								fetchProducts()
								isScrolling = false
								binding.progressBar.visibility = View.GONE
								productAdapter.notifyDataSetChanged()
							}
						}

					}
				})
			}

			productAdapter.notifyDataSetChanged()
		} else {
			binding.tvEmptyProducts.visibility = View.VISIBLE
		}
	}

	private fun setSelectedOrder(order: String) {
		selectedOrder = order
		orderAdapter = StringAdapter(this, orderOptions, orderOptions, selectedOrder)
		binding.rvOrder.setHasFixedSize(true)
		binding.rvOrder.adapter = orderAdapter
		binding.rvOrder.layoutManager?.scrollToPosition(orderOptions.indexOf(order))
		orderAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				setSelectedOrder(orderOptions[position])
			}
		})
	}

	private fun setSelectedSortBy(order: String) {
		selectedSortBy = order
		sortByAdapter = StringAdapter(this, sortByOptions, sortByOptions, selectedSortBy)
		binding.rvSortBy.setHasFixedSize(true)
		binding.rvSortBy.adapter = sortByAdapter
		binding.rvSortBy.layoutManager?.scrollToPosition(sortByOptions.indexOf(order))
		sortByAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				setSelectedSortBy(sortByOptions[position])
			}
		})
	}

	private fun setSelectedStar(star: String) {
		selectedStar = star
		binding.rvStar.setHasFixedSize(true)
		starAdapter = StringAdapter(this, starOptions, starOptions, selectedStar)
		binding.rvStar.adapter = starAdapter
		binding.rvStar.layoutManager?.scrollToPosition(starOptions.indexOf(star))
		starAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				setSelectedStar(starOptions[position])
			}
		})
	}

	private fun setSelectedCategory(categoryName: String) {
		selectedCategory = categoryName
		binding.rvCategories.setHasFixedSize(true)
		categoryAdapter = StringAdapter(this, categoriesName, categoriesName, selectedCategory)
		binding.rvCategories.adapter = categoryAdapter
		binding.rvCategories.layoutManager?.scrollToPosition(categoriesName.indexOf(categoryName))
		categoryAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				setSelectedCategory(categoriesName[position])
			}
		})
	}

	companion object {
		private const val TAG = "SearchResult"
	}
}