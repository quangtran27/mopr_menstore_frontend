package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.CheckoutItemBinding
import com.mopr.menstore.models.CartItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants

class CheckOutItemAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val productDetailList: List<ProductDetail>, private val products: List<Product>,
    private val images: List<ProductImage>,
) : RecyclerView.Adapter<CheckOutItemAdapter.CheckOutItemViewHolder>() {
    inner class CheckOutItemViewHolder(private val binding : CheckoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged")
        fun bind(cartItem : CartItem, productDetail : ProductDetail, product : Product, image : ProductImage){
            binding.tvCheckOutItemName.text = product.name
            if (productDetail.color.equals("default_color")){
                productDetail.color = "black"
            }
            binding.tvCategoryOfcheckOutItem.text = productDetail.size + ", " + productDetail.color
            if (productDetail.onSale){
                binding.tvCheckOutItemPrice.text = productDetail.salePrice.toString() + "đ"
            }
            else{
                binding.tvCheckOutItemPrice.text = productDetail.price.toString() + "đ"
            }

            binding.tvCheckOutItemQuantity.text = "x" + cartItem.quantity.toString()
            Glide.with(context)
                .load(Constants.BASE_IMAGE_URL + image.image)
                .into(binding.ivCheckOutItem)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckOutItemViewHolder {
        val binding = CheckoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckOutItemViewHolder(binding)
    }
    override fun getItemCount() = cartItems.size
    override fun onBindViewHolder(holder: CheckOutItemViewHolder, position: Int) {
        holder.bind(cartItems[position],productDetailList[position],products[position],images[position])
    }

}