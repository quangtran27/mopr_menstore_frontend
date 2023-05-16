package com.mopr.menstore.fragments.main
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.mopr.menstore.activities.CartActivity
import com.mopr.menstore.activities.SearchActivity
import com.mopr.menstore.adapters.CompactProductAdapter
import com.mopr.menstore.api.BannerApiService
import com.mopr.menstore.api.BannerApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.databinding.FragmentHomeBinding
import com.mopr.menstore.models.Banner
import com.mopr.menstore.models.Banner
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.BannerApiUtil
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.BannerApiUtil
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.ProductApiUtil
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
	private lateinit var binding: FragmentHomeBinding
	private var topSaleProducts: List<Product> = emptyList()
	private var latestProducts: List<Product> = emptyList()
	private var topSaleProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var topSaleProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()
	private var latestProductDetailsList: MutableList<List<ProductDetail>> = mutableListOf()
	private var latestProductImagesList: MutableList<List<ProductImage?>> = mutableListOf()
	private var banners: List<Banner> = emptyList()

	private lateinit var compactProductAdapterForLatest: CompactProductAdapter
	private lateinit var compactProductAdapterTopSale: CompactProductAdapter

	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var bannerApiUtil: BannerApiUtil
	private var banners: List<Banner> = emptyList()

	private lateinit var compactProductAdapterForLatest: CompactProductAdapter
	private lateinit var compactProductAdapterTopSale: CompactProductAdapter

	private lateinit var productApiUtil: ProductApiUtil
	private lateinit var bannerApiUtil: BannerApiUtil
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentHomeBinding.inflate(layoutInflater)

		binding.header.etSearch.isFocusable = false
		binding.header.etSearch.setOnClickListener {
			startActivity(Intent(requireContext(), SearchActivity::class.java))
		}
		binding.header.ibCart.setOnClickListener {
			startActivity(Intent(requireContext(), CartActivity::class.java))
		}

		productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		bannerApiUtil = BannerApiUtil(RetrofitClient.getRetrofit().create(BannerApiService::class.java))

		fetchData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

	private fun fetchData() {
		productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
		bannerApiUtil = BannerApiUtil(RetrofitClient.getRetrofit().create(BannerApiService::class.java))

		fetchData()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

	private fun fetchData() {
		lifecycleScope.launch {
			launch {
				fetchTopSaleProducts()
				bindTopSaleProducts(topSaleProducts, topSaleProductDetailsList, topSaleProductImagesList)
			}
			launch {
				fetchLatestProducts()
				bindLatestProducts(latestProducts, latestProductDetailsList, latestProductImagesList)
			}
			launch {
				fetchBanners()
			}
			launch {
				fetchTopSaleProducts()
				bindTopSaleProducts(topSaleProducts, topSaleProductDetailsList, topSaleProductImagesList)
			}
			launch {
				fetchLatestProducts()
				bindLatestProducts(latestProducts, latestProductDetailsList, latestProductImagesList)
			}
			launch {
				fetchBanners()
			}
		}
	}

	private suspend fun fetchTopSaleProducts() {
		binding.progressBar.visibility = View.VISIBLE
		topSaleProducts = productApiUtil.getTopSale()
		binding.progressBar.visibility = View.GONE

		for (product in topSaleProducts) {
			topSaleProductDetailsList.add(productApiUtil.getDetails(product.id))
			topSaleProductImagesList.add(productApiUtil.getImages(product.id))
		}
	}

	private suspend fun fetchBanners() {
		banners = bannerApiUtil.getAll()
		val bannerSlides = ArrayList<SlideModel>()
		for (banner in banners) {
			bannerSlides.add(SlideModel(Constants.BASE_URL1 + banner.image))
		}
		binding.isBanners.setImageList(bannerSlides)
	}

	private suspend fun fetchLatestProducts() {
		binding.progressBar2.visibility = View.VISIBLE
		latestProducts = productApiUtil.getLatest()
		binding.progressBar2.visibility = View.GONE

		for (product in latestProducts) {
			latestProductDetailsList.add(productApiUtil.getDetails(product.id))
			latestProductImagesList.add(productApiUtil.getImages(product.id))
		}
	private suspend fun fetchTopSaleProducts() {
		binding.progressBar.visibility = View.VISIBLE
		topSaleProducts = productApiUtil.getTopSale()
		binding.progressBar.visibility = View.GONE

		for (product in topSaleProducts) {
			topSaleProductDetailsList.add(productApiUtil.getDetails(product.id))
			topSaleProductImagesList.add(productApiUtil.getImages(product.id))
		}
	}

	private suspend fun fetchBanners() {
		banners = bannerApiUtil.getAll()
		val bannerSlides = ArrayList<SlideModel>()
		for (banner in banners) {
			bannerSlides.add(SlideModel(Constants.BASE_URL1 + banner.image))
		}
		binding.isBanners.setImageList(bannerSlides)
	}

	private suspend fun fetchLatestProducts() {
		binding.progressBar2.visibility = View.VISIBLE
		latestProducts = productApiUtil.getLatest()
		binding.progressBar2.visibility = View.GONE

		for (product in latestProducts) {
			latestProductDetailsList.add(productApiUtil.getDetails(product.id))
			latestProductImagesList.add(productApiUtil.getImages(product.id))
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindTopSaleProducts(products: List<Product>, topSaleProductDetailsList: List<List<ProductDetail>>, topSaleProductImagesList: List<List<ProductImage?>>) {
		compactProductAdapterTopSale = CompactProductAdapter(requireContext(), products, topSaleProductDetailsList, topSaleProductImagesList)
		val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
		compactProductAdapterTopSale = CompactProductAdapter(requireContext(), products, topSaleProductDetailsList, topSaleProductImagesList)
		val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

		binding.rvTopSaleProducts.setHasFixedSize(true)
		binding.rvTopSaleProducts.layoutManager = layoutManager
		binding.rvTopSaleProducts.adapter = compactProductAdapterTopSale
		compactProductAdapterTopSale.notifyDataSetChanged()
		binding.rvTopSaleProducts.setHasFixedSize(true)
		binding.rvTopSaleProducts.layoutManager = layoutManager
		binding.rvTopSaleProducts.adapter = compactProductAdapterTopSale
		compactProductAdapterTopSale.notifyDataSetChanged()
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindLatestProducts(products: List<Product>, latestProductDetailsList: List<List<ProductDetail>>, latestProductImagesList: List<List<ProductImage?>>) {
		compactProductAdapterForLatest = CompactProductAdapter(requireContext(), products, latestProductDetailsList, latestProductImagesList)
		val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
		compactProductAdapterForLatest = CompactProductAdapter(requireContext(), products, latestProductDetailsList, latestProductImagesList)
		val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

		binding.rvLatestProduct.setHasFixedSize(true)
		binding.rvLatestProduct.layoutManager = layoutManager
		binding.rvLatestProduct.adapter = compactProductAdapterForLatest
		compactProductAdapterForLatest.notifyDataSetChanged()
		binding.rvLatestProduct.setHasFixedSize(true)
		binding.rvLatestProduct.layoutManager = layoutManager
		binding.rvLatestProduct.adapter = compactProductAdapterForLatest
		compactProductAdapterForLatest.notifyDataSetChanged()
	}
	companion object {
		/**
		 * @return A new instance of fragment HomeFragment.
		 */
		// TODO: Rename and change types and number of parameters
		@JvmStatic
		fun newInstance() =
			HomeFragment().apply {
				arguments = Bundle().apply {
				}
			}
		const val TAG = "HomeFragment"
	}
}