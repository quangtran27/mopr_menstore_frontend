package com.mopr.menstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ActivityMainBinding
import com.mopr.menstore.fragments.main.*

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.bnMenu.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.homePage -> {
					loadFragment(HomeFragment())
				}
				R.id.productsPage -> {
					loadFragment(ProductsFragment())
				}
				R.id.notiPage -> {
					loadFragment(NotificationFragment())
				}
				R.id.mePage -> {
					loadFragment(MeFragment())
				}
			}
			true
		}

		binding.bnMenu.selectedItemId = R.id.homePage
	}

	private fun loadFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.replace(R.id.flMainFragmentContainer, fragment)
			.commit()
	}
}