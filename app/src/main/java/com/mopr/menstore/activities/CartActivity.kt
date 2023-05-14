package com.mopr.menstore.activities
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mopr.menstore.adapters.CartItemAdapter
import com.mopr.menstore.adapters.CartItemAdapter.OnItemClickedListener
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCartBinding
import com.mopr.menstore.models.CartItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.CartApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    var cartItems : List<CartItem>  = mutableListOf()//lấy tất cả items của cart theo cartId
    var products : MutableList<Product> = mutableListOf()
    var images : MutableList<ProductImage> = mutableListOf()
    var productDetailList : MutableList<ProductDetail> = mutableListOf()
    var checkedCartItems: MutableList<CartItem> = mutableListOf()
    var checkedDetailList: MutableList<ProductDetail> = mutableListOf()
    var checkAll: Boolean = false //Chọn tất cả cartItem
    var userId: Int = 1 //userId lưu trong sharedPreference
    private var temptTotal: Int = 0 //Tạm tính
    private lateinit var jsonCartItems: String
    private lateinit var binding: ActivityCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)//cho phép hiển thị nút mũi tên để quay trở về trang Home
        supportActionBar!!.setDisplayShowTitleEnabled(false) //ẩn đi title của activity
        Log.d("ChauAnh","hello")
        fetchData() //đổ data về các cartItesms vào
        funcheckBuy()
    }
    //Xử lý để đổ dữ liệu về các cartItems lên trang giỏ hàng
    private fun fetchData(){
        if(checkedCartItems.isNullOrEmpty())
            checkedCartItems.clear()
       lifecycleScope.launch {
           val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
           val productApiUtil = ProductApiUtil(productApiService)
           val cartApiService = RetrofitClient.getRetrofit().create(CartApiService::class.java)
           val cartApiUtil = CartApiUtil(cartApiService)
           val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
           val userApiUtil = UserApiUtil(userApiService)
           var cart = userApiUtil.getCart(this@CartActivity.userId)
           cartItems = cartApiUtil.getAllCartItem(cart!!.id)
           Log.d("ChauAnh",cartItems.toString())
            //lấy tất cả items của cart theo cartId
           for (item in cartItems)
           {
               //lấy chi tiết của 1 sản phẩm theo productDetailId
               val productDetail = productApiUtil.getProductDetail(item.productDetailId)
               productDetailList.add(productDetail!!)
               //lấy ra 1 sản phẩm theo productId
               val product = productApiUtil.get(productDetail.productId)
               products.add(product!!)
               //Lấy list ảnh của một sản phẩm
               val image = productApiUtil.getImages(product.id)
               //Lấy ảnh đầu tiên của list ảnh sản phẩm để thêm vào list ảnh của của các sản phẩm
               images.add(image[0]!!)
           }
           if(cartItems.isEmpty()){
               Log.d("Error","No items in cart")
           }
           else
           {
               bindCartItems()
               Log.d("chauanh","bind successfully")
           }
       }
    }
    //gắn dữ liệu vào recyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun bindCartItems() {
        checkAll = binding.cbCartItemAll.isChecked
        if (cartItems.isNotEmpty()) {
            checkAll = binding.cbCartItemAll.isChecked
            val cartItemAdapter = CartItemAdapter(this@CartActivity,cartItems,productDetailList, products,images,checkedCartItems,checkedDetailList, temptTotal, binding.tvTemptTotal, binding.cbCartItemAll,binding.ivCartItemDelete)
            binding.rvCartItems.setHasFixedSize(true)
            binding.rvCartItems.adapter = cartItemAdapter
            binding.rvCartItems.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
            cartItemAdapter.setOnItemClickedListener(object : OnItemClickedListener {
                override fun onItemClick(position: Int) {
                    Log.d("ChauAnh",position.toString())
                    cartItems.toMutableList().removeAt(position)
                    cartItemAdapter.notifyItemRemoved(position)
                }
            })
            cartItemAdapter.notifyDataSetChanged()
        } else {
            Log.d("ChauAnh","Không có món hàng nào trong giỏ hàng")
        }
    }
    private fun checkDelete(){
        binding.ivCartItemDelete.setOnClickListener{
            val deleteDialog = AlertDialog.Builder(this)
            if(cartItems.isNotEmpty() and binding.cbCartItemAll.isChecked){
                deleteDialog.setMessage("Bạn có chắc muốn xóa các món hàng này?")
                    .setPositiveButton("Xóa") { dialog, which ->
                        if(binding.cbCartItemAll.isChecked and cartItems.isNotEmpty()){
                            cartItems.toMutableList().clear()
                        }
                    }
                    .setNegativeButton("Không") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            if(checkedCartItems.isNotEmpty()){
                deleteDialog.setMessage("Bạn có chắc muốn xóa các món hàng này?")
                    .setPositiveButton("Xóa") { dialog, which ->
                        if(binding.cbCartItemAll.isChecked and cartItems.isNotEmpty()){
                            checkedCartItems.toMutableList().clear()
                        }
                    }
                    .setNegativeButton("Không") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
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
                Toast.makeText(this@CartActivity,"Bạn chưa chọn sản phẩm nào để mua!",Toast.LENGTH_LONG)
        }
    }
}