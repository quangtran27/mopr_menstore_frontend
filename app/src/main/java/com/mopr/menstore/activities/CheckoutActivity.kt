package com.mopr.menstore.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mopr.menstore.adapters.CheckOutItemAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCheckoutBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCheckoutBinding
    private var cartItemsChoosed: List<CartItem> = emptyList()
    private var products : MutableList<Product> = mutableListOf()
    private var cartItemIds: MutableList<Int> = mutableListOf()
    private var images : MutableList<ProductImage> = mutableListOf()
    private var productDetailList : MutableList<ProductDetail> = mutableListOf()
    private var defaultShippingFee : Int = 10000
    private var userId: Int = 1 //Lấy từ sharedPrefercence
    private var totalPayment : Int = 0
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var userApiUtil: UserApiUtil
    private lateinit var orderApiUtil: OrderApiUtil
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
        for (cartItem in cartItemsChoosed)
            cartItemIds.add(cartItem.id)
        fetchData()
        binding.tvNameCheckout.text = cartItemsChoosed.size.toString()
        binding.btnCartItemBuy.setOnClickListener{
            lifecycleScope.launch {
                orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
                //var order: Order = Order(user.id,user.name,user.phone,user.address.toString(),1,cartItemIds,note=)
                orderApiUtil.addOrder(user.id,user.name,user.phone,user.address.toString(),1,cartItemIds,note="Không có")
            }
        }

    }
    private fun fetchData() {
        lifecycleScope.launch {
            userApiUtil = UserApiUtil(RetrofitClient.getRetrofit().create(UserApiService::class.java))
            user = userApiUtil.getUser(userId)!!
            for (item in cartItemsChoosed) {
                productApiUtil= ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
                val productDetail = productApiUtil.getProductDetail(item.productDetailId)
                productDetailList.add(productDetail!!)
                val product = productApiUtil.get(productDetail.productId)
                products.add(product!!)
                val image = productApiUtil.getImages(product.id)
                images.add(image[0]!!)
            }
            bindCheckOutItems(cartItemsChoosed,productDetailList, products,images)
            Log.d("chauanh","bind successfully")
        }
    }
    private  fun totalPayment (cartItemsChoosed: List<CartItem>,productDetailList : MutableList<ProductDetail>): Int {
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

