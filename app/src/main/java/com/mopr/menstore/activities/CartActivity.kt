package com.mopr.menstore.activities
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mopr.menstore.adapters.CartItemAdapter
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCartBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.CartApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity(),CartItemAdapter.OnItemClickedListener {
    private var cart: Cart? = null
    private var cartItems : MutableList<CartItem>  = mutableListOf()
    private var products : MutableList<Product> = mutableListOf()
    private var images : MutableList<ProductImage> = mutableListOf()
    private var productDetailList : MutableList<ProductDetail> = mutableListOf()
    private var checkedCartItems: MutableList<CartItem> = mutableListOf()
    private var checkedDetailList: MutableList<ProductDetail> = mutableListOf()
    private var userId: Int = 1 //userId lưu trong sharedPreference
    private var checkedStates: MutableList<Boolean> = mutableListOf<Boolean>()
    private var temptTotal: Int = 0 //Tạm tính
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var cartApiUtil: CartApiUtil
    private lateinit var userApiUtil: UserApiUtil
    private lateinit var jsonCartItems: String
    private lateinit var binding: ActivityCartBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.tvTitle.text = "Giỏ hàng"
        binding.header.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
        cartApiUtil = CartApiUtil(RetrofitClient.getRetrofit().create(CartApiService::class.java))
        userApiUtil = UserApiUtil(RetrofitClient.getRetrofit().create(UserApiService::class.java))
        fetchData()
        funcheckBuy()
    }
    override fun onDeleteClick(
        position: Int,
    ) {
        lifecycleScope.launch {
            val cartItem = cartItems[position]
            cartApiUtil.deleteCartItem(cartItem.cartId, cartItem.id)
        }
        if (checkedCartItems.contains(cartItems[position]))
            checkedCartItems.remove(cartItems[position])
        if (checkedDetailList.contains(productDetailList[position]))
            checkedDetailList.remove(productDetailList[position])
        checkedStates.removeAt(position)
        products.removeAt(position)
        productDetailList.removeAt(position)
        images.removeAt(position)
        cartItems.removeAt(position)
        Log.d(TAG,checkedCartItems.toString())
        if (cartItems.size == 0) {
            Toast.makeText(
                this@CartActivity,
                "Không có món hàng nào trong giỏ hàng!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        var i = 0
        for(i in (0 .. checkedStates.size-1))
            checkedStates[i] = false
        binding.cbCartItemAll.isChecked=false
        bindCartItems()
    }

    override fun chooseAllItemsClick() {
        Log.d(TAG,checkedCartItems.toString())
        bindCartItems()
    }

    override fun tamtinh(
        checkedCartItems: MutableList<CartItem>,
        checkedDetailList: MutableList<ProductDetail>
    ) {
        TODO("Not yet implemented")
    }

    //Xử lý để đổ dữ liệu về các cartItems lên trang giỏ hàng
    private fun fetchData() {
            checkedCartItems.clear()
            checkedStates.clear()
        lifecycleScope.launch {
            cart = userApiUtil.getCart(this@CartActivity.userId)
            cartItems = cartApiUtil.getAllCartItem(cart!!.id) as MutableList<CartItem>
            for (item in cartItems) {
                val productDetail = productApiUtil.getProductDetail(item.productDetailId)
                productDetailList.add(productDetail!!)
                val product = productApiUtil.get(productDetail.productId)
                products.add(product!!)
                val image = productApiUtil.getImages(product.id)
                images.add(image[0])
                checkedStates.add(false)
            }
            bindCartItems()
       }
    }
    //gắn dữ liệu vào recyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun bindCartItems() {
        val cartItemAdapter = CartItemAdapter(
            this@CartActivity,
            this.cartItems,
            this.productDetailList,
            this.products,
            this.images,
            checkedCartItems,
            checkedDetailList,
            temptTotal,
            binding.tvTemptTotal,
            binding.cbCartItemAll,
            this@CartActivity,
            checkedStates
        )
        binding.rvCartItems.setHasFixedSize(true)
        binding.rvCartItems.adapter = cartItemAdapter
        binding.rvCartItems.layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        cartItemAdapter.notifyDataSetChanged()
    }
    private fun funcheckBuy(){
        binding.btnCartItemBuy.setOnClickListener{ //Khi người dùng nhấn vào nút mua hàng -> tới trang thanh toán
            if (checkedCartItems.isNotEmpty() or (binding.cbCartItemAll.isChecked and cartItems.isNotEmpty())){
                val intent = Intent(this@CartActivity, CheckoutActivity::class.java)
                val gson = Gson()
                if (binding.cbCartItemAll.isChecked){
                    jsonCartItems = gson.toJson(cartItems) //truyền theo list chứa những cartItems được chọn để thanh toán/mua
                }
                else{
                    jsonCartItems = gson.toJson(checkedCartItems) //truyền theo list chứa những cartItems được chọn để thanh toán/mua
                }
                intent.putExtra("jsonCartItems", jsonCartItems) //list này ở dạng json
                startActivity(intent)
            }
            else
                Toast.makeText(this@CartActivity,"Bạn chưa chọn sản phẩm nào để mua!",Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val TAG = "CartActivtiy"
    }
}