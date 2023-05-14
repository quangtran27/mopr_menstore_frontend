package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.CartItemBinding
import com.mopr.menstore.models.CartItem
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants


open class CartItemAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val productDetailList: List<ProductDetail>,
    private val products: List<Product>,
    private val images: List<ProductImage>,
    private var checkedCartItems: MutableList<CartItem>,
    private var checkedDetailList: MutableList<ProductDetail>,
    private var temptTotal: Int,
    private var tv_temptTotal: TextView,
    private var cb_cbCartItemAll: CheckBox,
    private var ivCartItemDelete: ImageView
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    private lateinit var listener: OnItemClickedListener

    inner class CartItemViewHolder(private val binding: CartItemBinding,listener: OnItemClickedListener) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged")
        fun bind(
            cartItem: CartItem,
            productDetail: ProductDetail,
            product: Product,
            image: ProductImage
        ) {
            //Hiển thị tên
            binding.tvCartItemName.text = product.name
            if (productDetail.color.equals("default_color")) {
                productDetail.color = "black"
            }
            //Hiển thị chi tiết về màu và size
            binding.tvCategoryOfCartItem.text = productDetail.size + ", " + productDetail.color
            //Hiển thị giá theo sale
            if (productDetail.onSale) {
                binding.tvCartItemPrice.text = productDetail.salePrice.toString() +"đ"
            } else {
                binding.tvCartItemPrice.text = productDetail.price.toString() + "đ"
            }
            //Hiển thị số lượng ban đầu
            binding.tvCartItemQuantity.text = "x"+cartItem.quantity.toString()
            Glide.with(context)
                .load(Constants.BASE_IMAGE_URL + image.image)
                .into(binding.ivCartItem)
            //Sự kiện chọn tất cả
            cb_cbCartItemAll.setOnCheckedChangeListener{
                _,_,->
                if (cb_cbCartItemAll.isChecked) {
                    tamtinh(cartItems as MutableList<CartItem>,
                    productDetailList as MutableList<ProductDetail>
                    )
                }
                else{
                    tamtinh(checkedCartItems,checkedDetailList)
                }

            }
            //Kiểm tra xem checkBox của các item có thay đổi trạng thái hay không
            binding.cbCartItem.setOnCheckedChangeListener { _, _ ->
                //Nếu từ false -> true
                if (binding.cbCartItem.isChecked)
                    if (cb_cbCartItemAll.isChecked){
                        cb_cbCartItemAll.isChecked = false
                    }
                    if (!checkedCartItems.contains(cartItem)) //và chưa có item trong checkedCartItems thì thêm vào
                    {
                        checkedCartItems.add(cartItem)
                        checkedDetailList.add(productDetail)
                        tamtinh(checkedCartItems,checkedDetailList)
                    }
                //Nếu từ true -> false
                if (!binding.cbCartItem.isChecked) {
                    //nếu trong checkedCartItems có chứa sẵn cartItem thì xóa nó đi
                    if (checkedCartItems.contains(cartItem)) {
                        checkedCartItems.remove(cartItem)
                        checkedDetailList.remove(productDetail)
                        tamtinh(checkedCartItems,checkedDetailList)
                        Log.d("chauanh",temptTotal.toString())
                    }
                }
            }
            //Sự kiện tăng số lượng sản phẩm
            binding.ivPlus.setOnClickListener {
                if(cartItem.quantity < productDetail.quantity)
                    cartItem.quantity += 1
                notifyDataSetChanged()
                if (cb_cbCartItemAll.isChecked)
                {
                    tamtinh(cartItems as MutableList<CartItem>,productDetailList as MutableList<ProductDetail>)
                }
                else
                {
                    binding.tvCartItemQuantity.text = (cartItem.quantity).toString()
                    tamtinh(checkedCartItems,checkedDetailList)
                    Log.d("chauanh",temptTotal.toString())
                }
            }
            //Sự kiện giảm số lượng sản phẩm
            binding.ivMinus.setOnClickListener {
                if (cartItem.quantity >=1)
                    cartItem.quantity -= 1
                if (cartItem.quantity == 0){
                    val deleteDialog = AlertDialog.Builder(context)
                    deleteDialog.setMessage("Bạn có chắc muốn xóa các món hàng này?")
                        .setPositiveButton("Xóa") { dialog, which ->
                            cartItems.toMutableList().remove(cartItem)
                            if(checkedCartItems.contains(cartItem)){
                                checkedCartItems.toMutableList().remove(cartItem)
                            }
                            for (checkedDetail in checkedDetailList){
                                if (checkedDetail.id == cartItem.productDetailId)
                                    checkedDetailList.remove(checkedDetail)
                            }
                        }
                        .setNegativeButton("Không") { dialog, which ->
                            dialog.dismiss()
                            cartItem.quantity += 1
                            notifyDataSetChanged()
                        }
                        .show()
                }
                notifyDataSetChanged()
                if (cb_cbCartItemAll.isChecked)
                {
                    tamtinh(cartItems as MutableList<CartItem>,productDetailList as MutableList<ProductDetail>)
                }
                else{

                    binding.tvCartItemQuantity.text = (cartItem.quantity).toString()
                    tamtinh(checkedCartItems,checkedDetailList)
                }
                Log.d("chauanh",temptTotal.toString())

            }
            //Sự kiện delete
            //checkDelete(binding,cartItems,cartItems.indexOf(cartItem))
            binding.ivDeleteCartItem.setOnClickListener{
                @Override
            fun onClick(view: View){
                listener.onItemClick(adapterPosition)
            } }
        }
    }
    //Tam tính
    fun tamtinh(checkedCartItems: MutableList<CartItem>, checkedDetailList: MutableList<ProductDetail>) {
        temptTotal = 0
        var i:Int=0
        for (item in checkedCartItems) {
            if (checkedDetailList[i].onSale) {
                temptTotal = temptTotal + (item.quantity * checkedDetailList[i].salePrice)
            } else {
                temptTotal = temptTotal + (item.quantity * checkedDetailList[i].price)
            }
            i++
        }
        tv_temptTotal.text = temptTotal.toString() + "đ"
    }
    fun checkDelete(binding: CartItemBinding,cartItems: List<CartItem>,position: Int){
        binding.ivDeleteCartItem.setOnClickListener{
            cartItems.toMutableList().removeAt(position)
            notifyDataSetChanged()
        }
    }
    public interface OnItemClickedListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickedListener(clickListener: OnItemClickedListener){
        listener= clickListener
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding, listener)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = cartItems.size

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(
            cartItems[position],
            productDetailList[position],
            products[position],
            images[position]
        )
    }

}
