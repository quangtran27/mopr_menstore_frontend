package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mopr.menstore.adapters.CartItemAdapter
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCartBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.CartApiUtil
import com.mopr.menstore.utils.Formatter
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding

    private var userId = 1
    private var cart: Cart? = null
    private var cartItems: MutableList<CartItem> = mutableListOf()
    private var products: MutableList<Product> = mutableListOf()
    private var productDetails: MutableList<ProductDetail> = mutableListOf()
    private var productImagesList: MutableList<List<ProductImage>> = mutableListOf()
    private var selectedCartItems: MutableList<CartItem> = mutableListOf()

    private lateinit var cartApiUtil: CartApiUtil
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var userApiUtil: UserApiUtil

    private var isUpdatingCartItem = false

    private val selectAllCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked && cartItems.size != selectedCartItems.size) {
            for (item in cartItems) {
                selectedCartItems.add(item)
            }
            bindData()
        } else if (!isChecked) {
            selectedCartItems.clear()
            bindData()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Giỏ hàng"
        binding.header.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.srlCartItems.setOnRefreshListener {
            cartItems.clear()
            products.clear()
            productDetails.clear()
            productImagesList.clear()
            fetchData()
        }
        binding.cbSelectAll.setOnCheckedChangeListener(selectAllCheckedChangeListener)
        binding.btnCheckout.setOnClickListener {
            if (selectedCartItems.isNotEmpty()){
                val gson = Gson()
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("jsonSelectedCartItems", gson.toJson(selectedCartItems))
//            intent.putExtra("jsonProducts", gson.toJson(products))
//            intent.putExtra("jsonProductDetails", gson.toJson(productDetails))
//            intent.putExtra("jsonProductImagesList", gson.toJson(productImagesList))
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this@CartActivity,"Bạn chưa chọn sản phẩm nào để mua!",Toast.LENGTH_LONG).show()
            }
        }

        cartApiUtil = CartApiUtil(RetrofitClient.getRetrofit().create(CartApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
        userApiUtil = UserApiUtil(RetrofitClient.getRetrofit().create(UserApiService::class.java))
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        cartItems.clear()
        productDetails.clear()
        products.clear()
        productImagesList.clear()
        selectedCartItems.clear()

        lifecycleScope.launch {
            cart = userApiUtil.getCart(userId)
            Log.d(TAG, "cart: $cart")
            if (cart != null) {
                cartItems = cartApiUtil.getCartItems(cart!!.id) as MutableList<CartItem>
                for (item in cartItems) {
                    val productDetail = productApiUtil.getDetail(item.productDetailId)!!
                    val product = productApiUtil.get(productDetail.productId)!!
                    val images = productApiUtil.getImages(product.id)

                    productDetails.add(productDetail)
                    products.add(product)
                    productImagesList.add(images)
                }
            }
            bindData()

            binding.srlCartItems.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun bindData() {
        Log.d(TAG,"cartItem: ${cartItems}")

        binding.cbSelectAll.setOnCheckedChangeListener(null)
        binding.cbSelectAll.isChecked = selectedCartItems.size == cartItems.size
        binding.cbSelectAll.setOnCheckedChangeListener(selectAllCheckedChangeListener)

        // Calculate provisional total
        var provisionalTotal = 0
        for (index in 0 until cartItems.size) {
            if (selectedCartItems.any {it.id == cartItems[index].id}) {
                provisionalTotal += if (productDetails[index].onSale) {
                    productDetails[index].salePrice * cartItems[index].quantity
                } else {
                    productDetails[index].price * cartItems[index].quantity
                }
            }
        }
        binding.tvProvisionalTotal.text = Formatter.formatVNDAmount(provisionalTotal.toLong())

        val adapter = CartItemAdapter(this, cartItems, products, productDetails, productImagesList, selectedCartItems)
        binding.rvCartItems.setHasFixedSize(true)
        binding.rvCartItems.adapter = adapter
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickListener(object : CartItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d(TAG, "onItemClick: CLICK ON POSITION $position")
            }

            override fun onDeleteButtonClick(position: Int) {
                if (!isUpdatingCartItem) {
                    lifecycleScope.launch {
                        isUpdatingCartItem = true

                        val isDeleteSuccess = cartApiUtil.deleteCartItem(cart?.id ?: 0, cartItems[position].id)
                        if (isDeleteSuccess) {
                            Toast.makeText(this@CartActivity, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                            cartItems.removeAt(position)
                            products.removeAt(position)
                            productDetails.removeAt(position)
                            productImagesList.removeAt(position)
                            bindData()
                        } else {
                            Toast.makeText(this@CartActivity, "Xóa không thành công, đã xảy ra lỗi!", Toast.LENGTH_SHORT).show()
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            isUpdatingCartItem = false
                        }, (Toast.LENGTH_LONG * 1000).toLong())
                    }
                }
            }

            override fun onChangeQuantity(position: Int, quantity: Int) {
                if (!isUpdatingCartItem) {
                    lifecycleScope.launch {
                        isUpdatingCartItem = true
                        val cartItem = cartApiUtil.updateCartItem(cart?.id ?: 0, cartItems[position].id, quantity)
                        if (cartItem != null) {
                            cartItems[position] = cartItem
                            bindData()
                        } else {
                            Toast.makeText(this@CartActivity, "Cập nhật số lượng không thành công, đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            isUpdatingCartItem = false
                        }, (Toast.LENGTH_LONG * 1000).toLong())
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSelected(position: Int, isChecked: Boolean) {
                if (isChecked) {
                    selectedCartItems.add(cartItems[position])
                } else {
                    selectedCartItems.removeIf {it.id == cartItems[position].id}
                }
                bindData()
            }
        })
    }



    companion object {
        const val TAG = "CartActivity"
    }
}