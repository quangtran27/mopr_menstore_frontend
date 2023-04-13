package com.mopr.menstore.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.CompactProductItemBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.Formatter

class CompactProductAdapter(
	private val context: Context,
	private val products: List<Product>
): RecyclerView.Adapter<CompactProductAdapter.ViewHolder>() {
	inner class ViewHolder(private val binding: CompactProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(product: Product) {
			if (product.details.isNotEmpty()) {
				var minPrice = 999999999
				for (productDetail in product.details) {
					if (productDetail.onSale) {
						if (productDetail.salePrice < minPrice) {
							minPrice = productDetail.salePrice
						}
					} else {
						if (productDetail.price < minPrice) {
							minPrice = productDetail.price
						}
					}
					binding.tvProductPrice.text = Formatter.formatVNDAmount(minPrice.toLong())
				}
			}
			Glide.with(context).load(Constants.BASE_URL + product.images[0].url).into(binding.ivProductImage)
			binding.tvProductName.text = product.name
		}
	}

	override fun getItemCount(): Int = products.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompactProductAdapter.ViewHolder {
		val binding = CompactProductItemBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: CompactProductAdapter.ViewHolder, position: Int) {
		val product = products[position]
		holder.bind(product)
	}
}