package com.android.on_track.ui.navigation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.databinding.FragmentHomeBinding
import com.android.on_track.ui.loginregister.LoginRegisterViewModel
import com.android.on_track.ui.loginregister.LoginRegisterViewModelFactory
import com.android.on_track.ui.loginregister.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var viewModel: LoginRegisterViewModel

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    // TODO: It may be better to put everything into onCreateView to get the most recent database instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        val db = Firebase.firestore
        val userData = FirebaseUserData(auth, db)
        val userDataRepository = FirebaseUserDataRepository(userData)
        val viewModelFactory = LoginRegisterViewModelFactory(userDataRepository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LoginRegisterViewModel::class.java]

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                if (user.isAnonymous == true) {
                    // they are using guest account
                    // TODO: Do something
                } else if (user.accountType == "Parent") {
                    // they are using parent account
                    // TODO: Do something
                } else if (user.accountType == "Child") {
                    // they are using child account
                    // TODO: Do something
                }
            } else {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView = binding.textView
        val signOutButton = binding.signOutButton

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                if (user.isAnonymous == true) {
                    // they are using guest account
                    textView.text = "Guest"
                } else if (user.accountType == "Parent") {
                    // they are using parent account
                    textView.text = "Parent"
                } else if (user.accountType == "Child") {
                    // they are using child account
                    textView.text = "Child"
                }
            } else {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        signOutButton.setOnClickListener {
            viewModel.signOut()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}