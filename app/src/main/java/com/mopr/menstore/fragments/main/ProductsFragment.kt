package com.mopr.menstore.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.R
import com.mopr.menstore.activities.CartActivity
import com.mopr.menstore.activities.SearchActivity
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.ProductAdapter
import com.mopr.menstore.api.CategoryApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentProductsBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.CategoryApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class ProductsFragment : Fragment() {
	private var categoryId: Int = 0
	private var currentPage = 1
	private var total = 0
	private var isScrolling = false
	private var categories: MutableList<Category> = mutableListOf()
	private var products: MutableList<Product> = mutableListOf()
	private var productDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var productImagesList: MutableList<List<ProductImage>> = mutableListOf()
	private lateinit var binding: FragmentProductsBinding
	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var categoryApiUtil: CategoryApiUtil
	private lateinit var categoryAdapter: CategoryAdapter
	private lateinit var productAdapter: ProductAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentProductsBinding.inflate(layoutInflater)

		arguments?.let {
			categoryId = it.getInt("categoryId")
		}

		binding.header.etSearch.isFocusable = false
		binding.header.etSearch.setOnClickListener {
			startActivity(Intent(requireContext(), SearchActivity::class.java))
		}
		binding.header.ibCart.setOnClickListener {
			startActivity(Intent(requireContext(), CartActivity::class.java))
		}
		binding.srlProducts.setOnRefreshListener {
			products = mutableListOf()
			productDetailsList = mutableListOf()
			productImagesList = mutableListOf()
			currentPage = 1
			total = 0
			fetchData()
		}

		productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		categoryApiUtil = CategoryApiUtil(RetrofitClient.getRetrofit().create(CategoryApiService::class.java))

		fetchData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = binding.root

	private fun fetchData() {
		lifecycleScope.launch {
			launch {
				fetchCategories()
				bindCategories()
			}
			launch {
				fetchProducts()
				bindProducts()
			}
			binding.srlProducts.isRefreshing = false
		}
	}

	private suspend fun fetchCategories() {
		categories = categoryApiUtil.getAllCategories() as MutableList<Category>
		categories.add(0, Category(0, "Tất cả", "", null))
	}

	private suspend fun fetchProducts() {
		val options: Map<String, String> = mapOf(
			"page" to "$currentPage", "category_id" to "$categoryId"
		)

		val productsResponse = productApiUtil.getAll(options)
		total = productsResponse!!.pagination.total
		for (product in productsResponse.data) {
			products.add(product)
			productDetailsList.add(productApiUtil.getDetails(product.id))
			productImagesList.add(productApiUtil.getImages(product.id))
		}
	}

	private fun bindCategories() {
		categoryAdapter = CategoryAdapter(requireContext(), categories, categoryId)
		categoryAdapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener {
			override fun onItemClick(categoryId: Int) {
				val fragment = newInstance(categoryId)
				requireActivity().supportFragmentManager.beginTransaction()
					.replace(R.id.flMainFragmentContainer, fragment).addToBackStack(null).commit()
			}
		})
		binding.rvCategories.setHasFixedSize(true)
		binding.rvCategories.adapter = categoryAdapter
		binding.rvCategories.layoutManager?.scrollToPosition(categories.indexOfFirst { it.id == categoryId })
	}

	private fun bindProducts() {
		productAdapter = ProductAdapter(requireContext(), products, productDetailsList, productImagesList)
		binding.rvProducts.apply {
			setHasFixedSize(true)
			adapter = productAdapter
			// Load more when scroll to bottom
			addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
						isScrolling = true
					}
				}

				override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
					val manager: LinearLayoutManager = layoutManager as LinearLayoutManager
					val currentItems = manager.childCount
					val totalItems = manager.itemCount
					val scrollOutItems = manager.findFirstVisibleItemPosition()

					if (isScrolling && (currentItems + scrollOutItems == totalItems) && (currentItems + scrollOutItems < total)) {
						currentPage += 1
						lifecycleScope.launch {
							binding.progressBar.visibility = View.VISIBLE
							fetchProducts()
							bindProducts()
							isScrolling = false
							binding.progressBar.visibility = View.GONE
						}
					}
				}
			})
		}
	}

	companion object {
		@JvmStatic
		fun newInstance(categoryId: Int) = ProductsFragment().apply {
			arguments = Bundle().apply {
				putInt("categoryId", categoryId)
			}
		}
		const val TAG = "ProductsFragment"
	}
}