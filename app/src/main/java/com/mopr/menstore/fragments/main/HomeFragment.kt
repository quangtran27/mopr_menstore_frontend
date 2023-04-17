package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.mopr.menstore.R
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.CompactProductAdapter
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentHomeBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
	private lateinit var binding: FragmentHomeBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentHomeBinding.inflate(layoutInflater)

		binding.ibCart.setOnClickListener {
//			startActivity(Intent(requireContext(), CartActivity))
		}

		fetchData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		return binding.root
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
				Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindTopSaleProducts(products: List<Product>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(requireActivity(), products)
			val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvTopSaleProducts.setHasFixedSize(true)
			binding.rvTopSaleProducts.layoutManager = layoutManager
			binding.rvTopSaleProducts.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		} else {
			binding.nsvTopSaleProducts.removeAllViews()
			binding.nsvTopSaleProducts.addView(makeEmptyMessageTextView("Sản phẩm trống"))
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindLatestProducts(products: List<Product>) {
		if (products.isNotEmpty()) {
			val compactProductAdapter = CompactProductAdapter(requireActivity(), products)
			val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvLatestProduct.setHasFixedSize(true)
			binding.rvLatestProduct.layoutManager = layoutManager
			binding.rvLatestProduct.adapter = compactProductAdapter

			compactProductAdapter.notifyDataSetChanged()
		} else {
			binding.nsvLatestProduct.removeAllViews()
			binding.nsvLatestProduct.addView(makeEmptyMessageTextView("Sản phẩm trống"))
		}
	}

	@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
	private fun bindCategories(categories: List<Category>) {
		if (categories.isNotEmpty()) {
			val categoryAdapter = CategoryAdapter(requireActivity(), categories)
			val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

			binding.rvCategories.setHasFixedSize(true)
			binding.rvCategories.layoutManager = layoutManager
			binding.rvCategories.adapter = categoryAdapter

			categoryAdapter.notifyDataSetChanged()
		} else {
			binding.nsvCategories.removeAllViews()
			binding.nsvCategories.addView(makeEmptyMessageTextView("Danh mục trống"))
		}
	}

	private fun makeEmptyMessageTextView(message: String): TextView {
		val textView = TextView(requireActivity())
		textView.text = message
		textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.text_gray))
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
		textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		textView.gravity = Gravity.CENTER
		return textView
	}
}