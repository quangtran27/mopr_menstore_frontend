package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.activities.ProductDetailActivity
import com.mopr.menstore.databinding.ItemProductBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.Product2
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class ProductAdapter2(val context: Context, val products: List<Product2>) : ListAdapter<Product2, ProductAdapter2.ProductViewHolder>(ProductDiffUtil) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
		val binding = ItemProductBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ProductViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
		holder.bind(products[position])
	}

	override fun getItemCount(): Int = products.size

	inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("SetTextI18n")
		fun bind(product: Product2) {
			// Get min detail
			var sold = 0
			var minPrice = Int.MAX_VALUE
			for (productDetail in product.details) {
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
			if (product.images.isNotEmpty()) {
				Glide.with(context).load(Constants.BASE_URL + product.images[0]).into(binding.ivProductImage)
			}

			binding.root.setOnClickListener {
				val intent = Intent(context, ProductDetailActivity::class.java)
//				pass data
//				intent.putExtra("product", product)
//				intent.putParcelableArrayListExtra("productDetails", ArrayList(productDetails))
//				intent.putParcelableArrayListExtra("productImages", ArrayList(productImages))
//				intent.putExtra("product", product)
//				itemView.context.startActivity(intent)
			}
		}
	}

	object ProductDiffUtil : DiffUtil.ItemCallback<Product2>() {
		override fun areItemsTheSame(oldItem: Product2, newItem: Product2) = oldItem.id == newItem.id
		override fun areContentsTheSame(oldItem: Product2, newItem: Product2) = oldItem.id == newItem.id

	}

	companion object {
		private const val TAG = "ProductAdapter"
	}
}