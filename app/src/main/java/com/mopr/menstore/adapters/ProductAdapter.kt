package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mopr.menstore.activities.ProductDetailActivity
import com.mopr.menstore.databinding.ItemProductBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class ProductAdapter (
	private val context: Context,
	private val products: List<Product>,
	private val productDetailsList: List<List<ProductDetail>>,
	private val productImagesList: List<List<ProductImage?>>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

	override fun getItemCount(): Int = products.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
		Log.d(TAG, "onCreateViewHolder: $products")
		val binding = ItemProductBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ProductViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
		if (products.isNotEmpty()) {
			holder.bind(products[position], productDetailsList[position], productImagesList[position])
		}
	}

	inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("SetTextI18n")
		fun bind(product: Product, productDetails: List<ProductDetail>, productImages: List<ProductImage?>) {
			// Get min detail
			var sold = 0
			var minPrice = Int.MAX_VALUE
			for (productDetail in productDetails) {
				sold += productDetail.sold
				minPrice = if (productDetail.onSale && productDetail.salePrice < minPrice) {
					productDetail.salePrice
				} else if (!productDetail.onSale && productDetail.price < minPrice) {
					productDetail.price
				} else {
					minPrice
				}
			}

			binding.tvProductName.text = product.name
			binding.tvProductSold.text = "Đã bán $sold"
			binding.tvProductPrice.text = Formatter.formatVNDAmount(minPrice.toLong())
			if (productImages.isNotEmpty()) {
				if (productImages[0] != null) {
					Glide.with(context).load(Constants.BASE_IMAGE_URL + productImages[0]!!.image).into(binding.ivProductImage)
				}
			}

			binding.root.setOnClickListener {
				val intent = Intent(context, ProductDetailActivity::class.java)

				val gson = Gson()
				intent.putExtra("product", gson.toJson(product))
				intent.putExtra("productDetails", gson.toJson(productDetails))
				intent.putExtra("productImages", gson.toJson(productImages))
				itemView.context.startActivity(intent)
			}
		}
	}

	companion object {
		private const val TAG = "ProductAdapter"
	}
}