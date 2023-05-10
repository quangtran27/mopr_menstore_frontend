package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.R
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


private const val ARG_CATEGORY_ID = "categoryId"

class ProductsFragment : Fragment() {
	private lateinit var binding: FragmentProductsBinding
	private var currentPage = 1
	private var total = 0
	private var categoryId: Int = 0
	private var products: MutableList<Product> = mutableListOf()
	private var categories: MutableList<Category> = mutableListOf()
	private var productDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var productImagesList: MutableList<List<ProductImage>> = mutableListOf()
	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var categoryApiUtil: CategoryApiUtil
	private lateinit var categoryAdapter: CategoryAdapter
	private var isScrolling = false
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
		binding.progressBar.visibility = View.GONE

		productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		categoryApiUtil = CategoryApiUtil(RetrofitClient.getRetrofit().create(CategoryApiService::class.java))

		lifecycleScope.launch {
			fetchData()
			bindData()
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		return binding.root
	}

	@SuppressLint("NotifyDataSetChanged")
	private suspend fun fetchData() {
		categories = categoryApiUtil.getAllCategories() as MutableList<Category>
		categories.add(0, Category(0, "Tất cả", "", null))

		// Get list products by category id
		fetchProducts()
	}

	@SuppressLint("NotifyDataSetChanged")
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

	private fun bindData() {
		bindCategories()
		bindProducts(products, productDetailsList, productImagesList)
	}

	private fun bindCategories() {
		categoryAdapter = CategoryAdapter(requireContext(), categories, categoryId)
		categoryAdapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener {
			override fun onItemClick(categoryId: Int) {
				val fragment = newInstance(categoryId)
				requireActivity().supportFragmentManager.beginTransaction()
					.replace(R.id.flMainFragmentContainer, fragment).addToBackStack(null)
					.commit()
			}
		})
		binding.rvCategories.setHasFixedSize(true)
		binding.rvCategories.adapter = categoryAdapter
		binding.rvCategories.layoutManager?.scrollToPosition(categories.indexOfFirst {
			it.id == categoryId
		})
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindProducts(
		products: List<Product>,
		productDetailsList: List<List<ProductDetail>>,
		productImagesList: List<List<ProductImage?>>
	) {
		productAdapter = ProductAdapter(requireContext(), products, productDetailsList, productImagesList)
		binding.rvProducts.apply {
			setHasFixedSize(true)
			adapter = productAdapter
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
							Log.d(TAG, "onScrolled: fetch more with current page: $currentPage")
							fetchProducts()
							isScrolling = false
							binding.progressBar.visibility = View.GONE
							productAdapter.notifyDataSetChanged()
						}
					}

				}
			})
		}
	}

	private fun makeMessageTextView(message: String): TextView {
		val textView = TextView(requireContext())
		textView.text = message
		textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
		textView.layoutParams = ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
		textView.gravity = Gravity.CENTER
		return textView
	}

	companion object {
		/**
		 * @param categoryId Category ID.
		 * @return A new instance of fragment ProductsFragment.
		 */
		@JvmStatic
		fun newInstance(categoryId: Int) = ProductsFragment().apply {
			arguments = Bundle().apply {
				putInt(ARG_CATEGORY_ID, categoryId)
			}
		}

		const val TAG = "ProductsFragment"
	}
}