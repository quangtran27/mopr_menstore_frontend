package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ProductItemBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.utils.Constants

class ProductAdapter (
	private val context: Context,
	private val products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
	inner class ProductViewHolder(private val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("ResourceAsColor", "SetTextI18n")
		fun bind(product: Product) {

			// Lowest price and sold
			var lowestPrice = 999999999
			var sold = 0
			for (detail in product.details) {
				sold += detail.sold
				lowestPrice = if (detail.onSale) detail.salePrice else detail.price.coerceAtMost(lowestPrice)
			}

			if (product.images.isNotEmpty()) {
				Glide.with(context).load(Constants.BASE_URL + product.images[0].url).into(binding.ivProductImage)
			} else {
				Glide.with(context).load(R.drawable.logo).into(binding.ivProductImage)
				binding.ivProductImage.setBackgroundColor(R.color.primary)
				binding.ivProductImage.scaleType = ImageView.ScaleType.FIT_CENTER
			}
			binding.tvProductName.text = product.name
			binding.tvProductPrice.text = lowestPrice.toString()
			binding.tvProductSold.text = "Đã bán: $sold"

			binding.root.setOnClickListener {
				// Forward to Product detail
			}

		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
		val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ProductViewHolder(binding)
	}

	override fun getItemCount(): Int = products.size

	override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
		holder.bind(products[position])
	}

}