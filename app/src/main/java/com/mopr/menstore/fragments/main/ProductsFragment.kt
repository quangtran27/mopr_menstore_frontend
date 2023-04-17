package com.mopr.menstore.fragments.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.mopr.menstore.R
import com.mopr.menstore.adapters.CategoryAdapter
import com.mopr.menstore.adapters.ProductAdapter
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentProductsBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.utils.Converter
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

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

	@SuppressLint("NotifyDataSetChanged")
	private fun fetchData() {
		val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
		val productApiUtil = ProductApiUtil(productApiService)

		lifecycleScope.launch {
			try {
				val categories = productApiUtil.getAllCategories()

				// Add <All> categories item in the first index
				val categoriesMutableList: MutableList<Category> = categories.toMutableList()
				categoriesMutableList.add(0, Category(0, "Tất cả", "", null))

				bindCategories(categoriesMutableList.toList())

				// Get list products by category id
				val products = productApiUtil.search(categoryId=categoryId)
				bindProducts(products)
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

	@SuppressLint("NotifyDataSetChanged")
	private fun bindProducts(products: List<Product>) {
		if (products.isNotEmpty()) {
			val productAdapter = ProductAdapter(requireContext(), products)

			binding.rvProducts.setHasFixedSize(true)
			binding.rvProducts.adapter = productAdapter
			binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
			binding.rvProducts.addItemDecoration(ProductGridSpacingItemDecoration(8, 2))

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

	companion object {
		@JvmStatic
		fun newInstance(categoryId: Int) =
			BlankFragment().apply {
				arguments = Bundle().apply {
					putInt("categoryId", categoryId)
				}
			}
	}

	inner class ProductGridSpacingItemDecoration(private val spacing: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {
		private val spacingPx = Converter.dpToPx(spacing)
		override fun getItemOffsets(
			outRect: Rect,
			view: View,
			parent: RecyclerView,
			state: RecyclerView.State
		) {
			super.getItemOffsets(outRect, view, parent, state)

			val position = parent.getChildAdapterPosition(view)
			val column = position % spanCount

			if (column < spanCount - 1) {
				outRect.right = spacingPx
				outRect.left = spacingPx
			} else {
				outRect.right = spacingPx
			}
			if (position < (parent.adapter?.itemCount ?: (0 - spanCount))) {
				outRect.bottom = spacingPx
				outRect.top = spacingPx
			}

			view.setBackgroundColor(Color.argb(0, 0, 0, 0))
		}
	}
}