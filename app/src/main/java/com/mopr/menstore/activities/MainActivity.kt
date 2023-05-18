package com.mopr.menstore.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ActivityMainBinding
import com.mopr.menstore.fragments.main.*
import com.mopr.menstore.fragments.user.MeFragment

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		binding.bnMenu.setOnItemSelectedListener {

			when (it.itemId) {
				R.id.homePage -> {
					loadFragment(HomeFragment.newInstance())
				}
				R.id.productsPage -> {
					loadFragment(ProductsFragment.newInstance(0))
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