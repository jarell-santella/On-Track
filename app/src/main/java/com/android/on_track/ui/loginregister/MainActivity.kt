package com.android.on_track.ui.loginregister

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.on_track.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var loginFragment: LoginFragment
    private lateinit var registerFragment: RegisterFragment
    private lateinit var fragments: ArrayList<Fragment>

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tabFragmentStateAdapter: TabFragmentStateAdapter

    private lateinit var tabTitles: Array<String>
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginFragment = LoginFragment()
        registerFragment = RegisterFragment()

        fragments = ArrayList()

        fragments.add(loginFragment)
        fragments.add(registerFragment)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        tabFragmentStateAdapter = TabFragmentStateAdapter(this, fragments)
        viewPager.adapter = tabFragmentStateAdapter

        // TabLayout for Login and Register tabs (fragments)
        tabTitles = resources.getStringArray(R.array.login_register_tabs)
        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy() { tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position]
        }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }
}