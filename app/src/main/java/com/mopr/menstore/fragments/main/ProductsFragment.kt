package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.mopr.menstore.R
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.ProductAdapter
import com.mopr.menstore.api.ApiException
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
	private var categoryId: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentProductsBinding.inflate(layoutInflater)

		arguments?.let {
			categoryId = it.getInt("categoryId")
		}
		fetchData()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return binding.root
	}

	companion object {
		/**
		 * @param categoryId Category ID.
		 * @return A new instance of fragment ProductsFragment.
		 */
		@JvmStatic
		fun newInstance(categoryId: Int) =
			ProductsFragment().apply {
				arguments = Bundle().apply {
					putInt(ARG_CATEGORY_ID, categoryId)
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
				// Add `All` categories item in the first index
				val categoriesMutableList: MutableList<Category> = categories.toMutableList()
				categoriesMutableList.add(0, Category(0, "Tất cả", "", null))

				bindCategories(categoriesMutableList.toList())

				// Get list products by category id
				val products = productApiUtil.searchProducts(categoryId=categoryId)
				val productDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
				val productImagesList: MutableList<List<ProductImage>> = mutableListOf()
				for (product in products) {
					productDetailsList.add(productApiUtil.getProductDetails(product.id))
					productImagesList.add(productApiUtil.getProductImages(product.id))
				}
				bindProducts(products, productDetailsList, productImagesList)
			} catch (e: ApiException) {
				Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
			}
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
		} else {
			binding.nsvCategories.removeAllViews()
			binding.nsvCategories.addView(makeMessageTextView("Danh mục trống"))
		}
	}

	@SuppressLint("NotifyDataSetChanged", "ResourceType")
	private fun bindProducts(products: List<Product>,productDetailsList: List<List<ProductDetail>>, productImagesList: List<List<ProductImage?>>) {
		if (products.isNotEmpty()) {
			val productAdapter = ProductAdapter(requireContext(), products, productDetailsList, productImagesList)
//			binding.rvProducts.setHasFixedSize(true)
			binding.rvProducts.adapter = productAdapter
			productAdapter.notifyDataSetChanged()
		} else {
			binding.svProducts.removeAllViews()
			binding.svProducts.addView(makeMessageTextView("Không có sản phẩm"))
		}
	}

	private fun makeMessageTextView(message: String): TextView {
		val textView = TextView(requireContext())
		textView.text = message
		textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
		textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		textView.gravity = Gravity.CENTER
		return textView
	}
}