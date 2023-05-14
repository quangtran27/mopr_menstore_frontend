package com.mopr.menstore.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mopr.menstore.adapters.CheckOutItemAdapter
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCheckoutBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCheckoutBinding
    private var cartItemsChoosed: List<CartItem> = emptyList()
    var products : MutableList<Product> = mutableListOf()
    var images : MutableList<ProductImage> = mutableListOf()
    var productDetailList : MutableList<ProductDetail> = mutableListOf()
    var defaultShippingFee : Int = 10000
    var userId: Int = 1 //Lấy từ sharedPrefercence
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)//cho phép hiển thị nút mũi tên để quay trở về trang Home
        //supportActionBar!!.setDisplayShowTitleEnabled(false) //ẩn đi title của activity
        var jsonCartItems = intent.getStringExtra("jsonCartItems")
        val type = object : TypeToken<List<CartItem>>() {}.type
        val gson = Gson()
        cartItemsChoosed = gson.fromJson(jsonCartItems, type)
        if (cartItemsChoosed.isEmpty()){
            Toast.makeText(this@CheckoutActivity,"Không có item nào được chọn",Toast.LENGTH_LONG)
        }
        else{
            fetchData()
            binding.tvNameCheckout.text = cartItemsChoosed.size.toString()
        }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
            val productApiUtil = ProductApiUtil(productApiService)
            val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
            val userApiUtil = UserApiUtil(userApiService)
            user = userApiUtil.getUser(userId)!!
            for (item in cartItemsChoosed)
            {
                //lấy chi tiết của 1 sản phẩm theo productDetailId
                val productDetail = productApiUtil.getProductDetail(item.productDetailId)
                productDetailList.add(productDetail!!)
                //lấy ra 1 sản phẩm theo productId
                val product = productApiUtil.get(productDetail.productId)
                products.add(product!!)
                //Lấy list ảnh theo id một sản phẩm
                val image = productApiUtil.getImages(product.id)
                //Lấy ảnh đầu tiên của list ảnh sản phẩm để thêm vào list ảnh của của các sản phẩm
                images.add(image[0]!!)
            }
            if(cartItemsChoosed.isEmpty()){
                Log.d("Error","No items in cart")
            }
            else
            {
                bindCheckOutItems(cartItemsChoosed,productDetailList, products,images)
                Log.d("chauanh","bind successfully")
            }
        }
    }
    private  fun totalPayment (cartItemsChoosed: List<CartItem>,productDetailList : MutableList<ProductDetail>): Int {
        var totalPayment : Int = 0
        var i = 0;
        for (item in cartItemsChoosed){
            if(productDetailList[i].onSale){
                totalPayment += (item.quantity * productDetailList[i].salePrice)
            }
            else{
                totalPayment += (item.quantity * productDetailList[i].price)
            }
            i++
        }
        Log.d("ChauAnh",totalPayment.toString())
        return totalPayment
    }

    private fun bindCheckOutItems(cartItemsChoosed: List<CartItem>, productDetailList: MutableList<ProductDetail>, products: MutableList<Product>, images: MutableList<ProductImage>) {
        if (cartItemsChoosed.isNotEmpty()) {
            val checkOutItemsAdapter = CheckOutItemAdapter(this@CheckoutActivity,cartItemsChoosed,productDetailList, products,images)
            binding.rvCheckOutItems.setHasFixedSize(true)
            binding.rvCheckOutItems.adapter = checkOutItemsAdapter
            binding.rvCheckOutItems.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
            var totalPayment = totalPayment(cartItemsChoosed,productDetailList)
            checkOutItemsAdapter.notifyDataSetChanged()
            binding.tvTemptPayment.text = totalPayment.toString() + "đ"
            if (totalPayment >= 1000000){
                defaultShippingFee = 0
            }
            else{
                defaultShippingFee = 30000
            }
            binding.tvShippingFeeCheckOut.text = defaultShippingFee.toString() + "đ"
            binding.tvTotalPaymentCheckout.text = (totalPayment + defaultShippingFee).toString() + "đ"
            binding.tvTotalPayment.text = (totalPayment + defaultShippingFee).toString() + "đ"
            if (user != null) {
                Log.d("ChauAnh",user.phone.toString())
                binding.tvNameCheckout.text = user.name.toString()
                binding.tvPhoneCheckout.text = user.phone
                binding.tvAddressCheckout.text = user.address
                binding.tvNoteCheckout.text = "Không có"
            }
        } else {
            Log.d("ChauAnh","Không có món hàng nào trong giỏ hàng")
        }
    }
}

