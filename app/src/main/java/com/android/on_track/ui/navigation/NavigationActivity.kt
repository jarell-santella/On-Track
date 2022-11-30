package com.android.on_track.ui.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.databinding.ActivityNavigationBinding
import com.android.on_track.ui.loginregister.LoginRegisterViewModel
import com.android.on_track.ui.loginregister.LoginRegisterViewModelFactory
import com.android.on_track.ui.loginregister.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding

    // TODO: Do not use LoginRegisterViewModel and LoginRegisterViewModelFactory
    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var textView: TextView
    private lateinit var signOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_navigation)
//
//        textView = findViewById(R.id.text_home)
//        signOutButton = findViewById(R.id.sign_out_button)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_geofence
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    override fun onStart() {
//        super.onStart()
//
//        val auth = Firebase.auth
//        val db = Firebase.firestore
//        val userData = FirebaseUserData(auth, db)
//        val userDataRepository = FirebaseUserDataRepository(userData)
//        // TODO: Do not use LoginRegisterViewModel and LoginRegisterViewModelFactory
//        val viewModelFactory = LoginRegisterViewModelFactory(userDataRepository)
//        viewModel = ViewModelProvider(this, viewModelFactory)[LoginRegisterViewModel::class.java]
//
//        viewModel.currentUser.observe(this) { user ->
//            if (user != null) {
//                if (user.isAnonymous == true) {
//                    // they are using guest account
//                    textView.text = "Anonymous"
//                } else if (user.accountType == "Parent") {
//                    // they are using parent account
//                    textView.text = "Parent"
//                } else if (user.accountType == "Child") {
//                    // they are using child account
//                    textView.text = "Child"
//                }
//            } else {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//
//        signOutButton.setOnClickListener {
//            viewModel.signOut()
//        }
//    }
}