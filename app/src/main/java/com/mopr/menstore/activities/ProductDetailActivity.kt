package com.mopr.menstore.activities

import SharePrefManager
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mopr.menstore.R
import com.mopr.menstore.adapters.ReviewAdapter
import com.mopr.menstore.adapters.StringAdapter
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.ReviewApiService
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityProductDetailBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.CartApiUtil
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.ReviewApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch


class ProductDetailActivity : AppCompatActivity() {
	private lateinit var binding: ActivityProductDetailBinding
	private lateinit var colorsAdapter: StringAdapter
	private lateinit var sizesAdapter: StringAdapter
	private lateinit var reviewsAdapter: ReviewAdapter
	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var userApiUtil: UserApiUtil
	private lateinit var reviewApiUtil: ReviewApiUtil
	private lateinit var cartApiUtil: CartApiUtil
	private lateinit var selectedProductDetail: ProductDetail
	private var product: Product? = null
	private var productDetails: List<ProductDetail> = listOf()
	private var productImages: List<ProductImage> = listOf()
	private var reviews: List<Review> = emptyList()
	private var users: MutableList<User> = mutableListOf()
	private var reviewImagesList: MutableList<List<ReviewImage>> = mutableListOf()
	private val sizes = mutableListOf<String>()
	private val colors = mutableListOf<String>()
	private var isAddingToCart = false


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityProductDetailBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
		binding.header.ibCart.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
		binding.btnAddToCart.setOnClickListener {
			if (selectedProductDetail.quantity > 0) {
				val userId = (SharePrefManager.getInstance(this).getUser().id).toInt()
				if (!isAddingToCart) {
					if (!::cartApiUtil.isInitialized) {
						cartApiUtil = CartApiUtil(RetrofitClient.getRetrofit().create(CartApiService::class.java))
					}
					lifecycleScope.launch {
						val cart = userApiUtil.getCart(userId)
						if (cart != null) {
							addToCart(cart.id, selectedProductDetail.id, 1)
						}
					}
				}
			}
		}

		getProductInfo() // Get product info from intent
		setSelectedDetail(productDetails[0])
		bindImageSlide()
		bindProductInfo()
		fetchReviews()
	}

	private fun bindImageSlide() {
		val imageSlides = ArrayList<SlideModel>()
		for (image in productImages as java.util.ArrayList<ProductImage>) {
			imageSlides.add(
				SlideModel(Constants.BASE_URL1 + image.image)
			)
		}
		binding.isProductImages.setImageList(imageSlides)
	}

	private fun bindProductInfo() {
		binding.tvProductName.text = product!!.name
		binding.tvProductDescription.text = product!!.desc

		binding.tvProductDescription.viewTreeObserver.addOnGlobalLayoutListener(object :
			ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				val lineCount = binding.tvProductDescription.lineCount
				if (lineCount > 2) {
					binding.tvProductDescription.maxLines = 2
					binding.tvProductDescription.ellipsize = TextUtils.TruncateAt.END

					binding.tvSeeMore.setOnClickListener {
						binding.tvSeeMore.visibility = View.GONE
						binding.tvProductDescription.maxLines = Int.MAX_VALUE
					}
				} else {
					binding.tvSeeMore.visibility = View.GONE
				}
				binding.tvProductDescription.viewTreeObserver.removeOnGlobalLayoutListener(this)
			}
		})
	}

	@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
	private fun fetchReviews() {
		lifecycleScope.launch {
			productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
			userApiUtil = UserApiUtil( RetrofitClient.getRetrofit().create(UserApiService::class.java))
			reviewApiUtil = ReviewApiUtil(RetrofitClient.getRetrofit().create(ReviewApiService::class.java))

			if (product != null) {
				reviews = productApiUtil.getReviews(product!!.id)
				binding.tvReviewsCountTop.text = "${reviews.size} đánh giá"
				binding.tvReviewsCountBot.text = "${reviews.size} đánh giá"
				var averageRate = reviews.map { it.star.toFloat() }.average()
				Log.d(TAG, "fetchReviews: $averageRate")
//				averageRate = String.format("%.1f", averageRate).toDouble()
				binding.rbRate.rating = averageRate.toFloat()
				binding.rbRate2.rating = averageRate.toFloat()
				if (!averageRate.isNaN()) {
					binding.tvRatingPercent.text = "$averageRate/5"
				} else {
					binding.tvRatingPercent.text = ""
				}
				for (review in reviews) {
					val user = userApiUtil.getUser(review.userId)
					users.add(user!!)

					val images = reviewApiUtil.getImages(review.id)
					reviewImagesList.add(images)
				}

				reviewsAdapter = ReviewAdapter(this@ProductDetailActivity, reviews, users, reviewImagesList, 2)
				binding.rvReviews.adapter = reviewsAdapter
				reviewsAdapter.notifyDataSetChanged()

				if (reviews.size <= 2) {
					binding.tvShowAllReviews.visibility = View.GONE
				}

				binding.tvShowAllReviews.setOnClickListener {
					val intent = Intent(this@ProductDetailActivity, AllReviewActivity::class.java)
					intent.putParcelableArrayListExtra("reviews", ArrayList(reviews))
					intent.putParcelableArrayListExtra("users", ArrayList(users))
					val flattenedList = ArrayList<ReviewImage>()
					for (subList in reviewImagesList) {
						flattenedList.addAll(subList)
					}
					val gson = Gson()
					val jsonReviewImagesList = gson.toJson(reviewImagesList)
					intent.putExtra("jsonReviewImagesList", jsonReviewImagesList)
					startActivity(intent)
				}
			}
		}
	}

	private fun getProductInfo() {
		val gson = Gson()
		product = gson.fromJson(intent.getStringExtra("product"), Product::class.java)
		productDetails = gson.fromJson(intent.getStringExtra("productDetails"), object : TypeToken<List<ProductDetail>>() {}.type)
		productImages = gson.fromJson(intent.getStringExtra("productImages"), object : TypeToken<List<ProductImage>>() {}.type)

		for (detail in productDetails) {
			if (detail.size !in sizes) {
				sizes.add(detail.size)
			}
			if (detail.color !in colors) {
				colors.add(detail.color)
			}
		}
	}

	@SuppressLint("SetTextI18n")
	private fun setSelectedDetail(detail: ProductDetail) {
		selectedProductDetail = detail

		// binding data to UI
		binding.tvProductQuantity.text = detail.quantity.toString()
		binding.tvProductSold.text = "Đã bán: ${detail.sold}"
		if (detail.onSale) {
			binding.tvProductOldPrice.visibility = View.VISIBLE
			binding.tvProductPrice.text = Formatter.formatVNDAmount(detail.salePrice.toLong())
			binding.tvProductOldPrice.text = Formatter.formatVNDAmount(detail.price.toLong())
			binding.tvProductOldPrice.paintFlags = binding.tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
		} else {
			binding.tvProductOldPrice.visibility = View.GONE
			binding.tvProductPrice.text = Formatter.formatVNDAmount(detail.price.toLong())
		}

		// Find and match available choices
		sizesAdapter = StringAdapter(this, sizes, findAvailableSizes(detail.color), detail.size)
		colorsAdapter = StringAdapter(this@ProductDetailActivity, colors, findAvailableColors(detail.size), detail.color)
		sizesAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				productDetails.find { it.size == sizes[position] && it.color == selectedProductDetail.color }?.let { setSelectedDetail(it) }
			}
		})
		colorsAdapter.setOnItemClickListener(object : StringAdapter.OnItemClickListener {
			override fun onItemClick(position: Int) {
				productDetails.find { it.color == colors[position] && it.size == selectedProductDetail.size }?.let { setSelectedDetail(it) }
			}
		})
		binding.rvProductColors.adapter = colorsAdapter
		binding.rvProductSizes.adapter = sizesAdapter

		updateBtnAddToCart()
	}

	@SuppressLint("SetTextI18n")
	private fun updateBtnAddToCart() {
		if (selectedProductDetail.quantity > 0) {
			binding.btnAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white))
			binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
			binding.btnAddToCart.text = "Thêm vào giỏ hàng"
		} else {
			binding.btnAddToCart.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
			binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(this, R.color.background_gray))
			binding.btnAddToCart.text = "Hết hàng"
		}
	}

	private fun addToCart(cartId: Int, productDetailId: Int, quantity: Int) {
		isAddingToCart = true
		val toast = Toast(this@ProductDetailActivity)
		toast.duration = Toast.LENGTH_SHORT
		toast.setGravity(Gravity.CENTER, 0, 0)
		lifecycleScope.launch {
			val cartItem = cartApiUtil.addToCart(cartId, productDetailId, quantity)
			if (cartItem != null) {
				toast.setText("Thêm thành công!")
			} else {
				toast.setText("Thêm không thành công...")
			}
			toast.show()
			Handler(Looper.getMainLooper()).postDelayed({
				isAddingToCart = false
			}, (Toast.LENGTH_SHORT * 1000).toLong())
		}
	}
	private fun findAvailableSizes(color: String): List<String> = productDetails.filter { it.color == color }.map { it.size }
	private fun findAvailableColors(size: String): List<String> = productDetails.filter { it.size == size }.map { it.color }
	companion object {
		const val TAG = "ProductDetail"
	}
}