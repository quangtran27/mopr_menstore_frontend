package com.mopr.menstore.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.activities.ProductDetailActivity
import com.mopr.menstore.databinding.ItemCompactProductBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class CompactProductAdapter(
	private val context: Context,
	private var products: List<Product>,
	private var productDetailsList: List<List<ProductDetail>>,
	private var productImagesList: List<List<ProductImage?>>
): RecyclerView.Adapter<CompactProductAdapter.CompactProductViewHolder>() {
	fun updateData(products: List<Product>, productDetailsList: List<List<ProductDetail>>, productImagesList: List<List<ProductImage?>>) {
		this.products = products
		this.productDetailsList = productDetailsList
		this.productImagesList = productImagesList
	}


	inner class CompactProductViewHolder(private val binding: ItemCompactProductBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(product: Product, productDetails: List<ProductDetail>, productImages: List<ProductImage?>) {
			// Get min detail
			var minPrice = Int.MAX_VALUE
			for (productDetail in productDetails) {
				minPrice = if (productDetail.onSale && productDetail.salePrice < minPrice) {
					productDetail.salePrice
				} else if (!productDetail.onSale && productDetail.price < minPrice) {
					productDetail.price
				} else {
					minPrice
				}
			}

			binding.tvProductPrice.text = Formatter.formatVNDAmount(minPrice.toLong())
			binding.tvProductName.text = product.name
			if (productImages.isNotEmpty()) {
				if (productImages[0] != null) {
					Glide.with(context).load(Constants.BASE_IMAGE_URL + productImages[0]!!.image).into(binding.ivProductImage)
				}
			}

			binding.root.setOnClickListener {
				val intent = Intent(context, ProductDetailActivity::class.java)
				intent.putExtra("product", product)
				intent.putParcelableArrayListExtra("productDetails", ArrayList(productDetails))
				intent.putParcelableArrayListExtra("productImages", ArrayList(productImages))
				intent.putExtra("product", product)
				itemView.context.startActivity(intent)
			}
		}
	}

	override fun getItemCount(): Int = products.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompactProductViewHolder {
		val binding = ItemCompactProductBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return CompactProductViewHolder(binding)
	}

	override fun onBindViewHolder(holder: CompactProductAdapter.CompactProductViewHolder, position: Int) {
		holder.bind(products[position], productDetailsList[position], productImagesList[position])
	}
}