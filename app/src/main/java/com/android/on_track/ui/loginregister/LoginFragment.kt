package com.android.on_track.ui.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.ui.navigation.NavigationActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var navigationIntent: Intent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val auth = Firebase.auth
        val db = Firebase.firestore
        val userData = FirebaseUserData(auth, db, view, requireContext())
        val userDataRepository = FirebaseUserDataRepository(userData)
        val viewModelFactory = LoginRegisterViewModelFactory(userDataRepository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LoginRegisterViewModel::class.java]

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                navigationIntent = Intent(context, NavigationActivity::class.java)
                if (user.isAnonymous == true) {
                    // they are using guest account
                    navigationIntent.putExtra("account_type_key", "guest")
                } else if (user.accountType == "parent") {
                    // they are using parent account
                    navigationIntent.putExtra("account_type_key", "parent")
                } else if (user.accountType == "child") {
                    // they are using child account
                    navigationIntent.putExtra("account_type_key", "child")
                }
                // TODO: We do not need to put the account type key as an extra in the intent
                //  Kept here in case we need it for the future
                startActivity(navigationIntent)
                requireActivity().finish()
            }
        }

        setHasOptionsMenu(true)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = view.findViewById<MaterialButton>(R.id.login_button)

        loginButton.setOnClickListener {
            var success = true

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isBlank()) {
                success = false
                emailInput.error = "Please enter an email address"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                success = false
                emailInput.error = "Please enter a valid email address"
            }

            if (password.isBlank()) {
                success = false
                passwordInput.error = "Please enter a password"
            }

            if (success) {
                viewModel.login(email, password)
            }
        }

        return view
    }

    // Create ActionBar at the top with "Guest Login" button
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.loginregister_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    // Listeners for the ActionBar Menu button "Guest Login"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.guest_login_button) {
            viewModel.guestLogin()
        }
        return super.onOptionsItemSelected(item)
    }
}