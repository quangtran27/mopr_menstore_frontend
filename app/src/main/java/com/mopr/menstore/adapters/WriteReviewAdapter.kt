package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ItemWriteReviewBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants

class WriteReviewAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val productDetails: List<ProductDetail>,
    private val productFirstImages: List<ProductImage>,
    private val listImageUploads: MutableList<MutableList<Uri>> = mutableListOf(),
) : RecyclerView.Adapter<WriteReviewAdapter.WriteReviewViewHolder>() {
    private var listener: OnItemClickListener? = null
    private var edtListener: EditTextListener? = null
    private var ratingBarListener: RatingBarListener? = null
    private var edtDataSet: MutableList<String> = mutableListOf()
    private var ratingDataSet: MutableList<Int> = mutableListOf()

    inner class WriteReviewViewHolder(private val binding: ItemWriteReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "CheckResult")
        fun bind(
            product: Product,
            productDetail: ProductDetail,
            productImage: ProductImage,
            position: Int
        ) {
            Glide.with(context).load(Constants.BASE_IMAGE_URL + productImage.image).into(binding.ivImagePro)
            binding.tvNameProduct.text = product.name
            binding.tvClassifyProduct.text = productDetail.size + ", " + productDetail.color
            ratingDataSet.add(position, binding.rantingBar.numStars)
            edtDataSet.add(position, binding.edtDescription.text.toString())
            binding.rantingBar.setOnRatingBarChangeListener { _, rating, _ ->
                val selectedStars = rating.toInt()
                when (selectedStars) {
                    1 -> {
                        binding.tvReview.text = "Tệ"
                        binding.tvReview.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_gray
                            )
                        )
                    }
                    2 -> {
                        binding.tvReview.text = "Không hài lòng"
                        binding.tvReview.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_gray
                            )
                        )
                    }
                    3 -> {
                        binding.tvReview.text = "Bình thường"
                        binding.tvReview.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_gray
                            )
                        )
                    }
                    4 -> {
                        binding.tvReview.text = "Hài lòng"
                        binding.tvReview.setTextColor(ContextCompat.getColor(context, R.color.star))
                    }
                    5 -> {
                        binding.tvReview.text = "Tuyệt vời"
                        binding.tvReview.setTextColor(ContextCompat.getColor(context, R.color.star))
                    }
                }
                ratingDataSet[position] = selectedStars
            }
            binding.edtDescription.addTextChangedListener {
                edtDataSet[position] = it.toString()
            }
            displayImages(listImageUploads[position])
            binding.llAddImage.setOnClickListener {
                listener!!.onUploadImage(position)
                displayImages(listImageUploads[position])
            }
            binding.llAddImageBonus.setOnClickListener {
                listener?.onUploadImage(position)
                displayImages(listImageUploads[position])
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun displayImages(imageUploads: MutableList<Uri>) {
            if (imageUploads.size == 0) {
                binding.llImageReview.visibility = View.GONE
                binding.llAddImage.visibility = View.VISIBLE
            }
            if (imageUploads.size in 1..4) {
                binding.llAddImage.visibility = View.GONE
                binding.llAddImageBonus.visibility = View.VISIBLE
                binding.llImageReview.visibility = View.VISIBLE
            }
            if (imageUploads.size == 5) {
                binding.llAddImageBonus.visibility = View.GONE
            }
            val imageAdapter = ReviewImageUploadAdapter(context, imageUploads)
            imageAdapter.setOnItemClickListener(object :
                ReviewImageUploadAdapter.OnItemClickListener {
                override fun onDeleteClick(position: Int) {
                    imageUploads.removeAt(position)
                    imageAdapter.notifyDataSetChanged()
                    displayImages(imageUploads)
                }

            })
            if (imageUploads.size > 0) {
                binding.rcImageReview.setHasFixedSize(true)
                val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, imageUploads.size)
                binding.rcImageReview.adapter = imageAdapter
                binding.rcImageReview.layoutManager = layoutManager
                imageAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteReviewViewHolder {
        val binding = ItemWriteReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WriteReviewViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: WriteReviewViewHolder, position: Int) {
        holder.bind(
            products[position],
            productDetails[position],
            productFirstImages[position],
            position
        )
    }

    interface OnItemClickListener {
        fun onUploadImage(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    interface EditTextListener {
        fun onGetAllEditText(editTextValues: MutableList<String>)
    }

    fun getAllEditText(edtListener: EditTextListener) {
        this.edtListener = edtListener
        edtListener.onGetAllEditText(edtDataSet)
    }

    interface RatingBarListener {
        fun onRatingChanged(ratingValues: MutableList<Int>)
    }

    fun getAllRatingBar(ratingBarListener: RatingBarListener) {
        this.ratingBarListener = ratingBarListener
        ratingBarListener.onRatingChanged(ratingDataSet)
    }
}