package com.mopr.menstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.response.AllCategories
import com.mopr.menstore.databinding.ActivityMainBinding
import com.mopr.menstore.models.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.id.setOnClickListener{
			callApi()
		}
	}

	private fun callApi() {
		val productAPIs = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
		productAPIs.getAllCategories().enqueue(object: Callback<AllCategories> {
			override fun onResponse(
				call: Call<AllCategories>,
				response: Response<AllCategories>
			) {
				if (response.isSuccessful) {
					val allCategories: AllCategories = response.body()!!
					if (allCategories.success) {
						Log.d("Quangdeptrai", allCategories.toString())
					}
				}
			}

			override fun onFailure(call: Call<AllCategories>, t: Throwable) {
				Log.d("errorrrrrr", t.toString())
			}
		}
		)
	}
}