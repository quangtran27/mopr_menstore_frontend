package com.mopr.menstore.activities

import SharePrefManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ActivityMainBinding
import com.mopr.menstore.fragments.main.*
import com.mopr.menstore.fragments.user.MeFragment

class MainActivity : AppCompatActivity() {
	private lateinit var binding: ActivityMainBinding
	private lateinit var sharePrefManager: SharePrefManager
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		sharePrefManager = SharePrefManager.getInstance(this)
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
					if (sharePrefManager.isLoggedIn()){
						loadFragment(NotificationFragment())
					} else{
						val intent = Intent(this@MainActivity, AuthenticationActivity::class.java)
						startActivity(intent)
					}
				}
				R.id.mePage -> {
					if (sharePrefManager.isLoggedIn()){
						loadFragment(NotificationFragment())
					} else{
						val intent = Intent(this@MainActivity, AuthenticationActivity::class.java)
						startActivity(intent)
					}
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