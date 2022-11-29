package com.android.on_track.ui.loginregister

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.ui.navigation.NavigationActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var toolbar: Toolbar

    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var navigationIntent: Intent

    // TODO: It may be better to put everything into onCreateView to get the most recent database instance
    override fun onCreate(savedInstanceState: Bundle?) {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val userData = FirebaseUserData(auth, db)
        val userDataRepository = FirebaseUserDataRepository(userData)
        val viewModelFactory = LoginRegisterViewModelFactory(userDataRepository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LoginRegisterViewModel::class.java]

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                navigationIntent = Intent(context, NavigationActivity::class.java)
                if (user.isAnonymous) {
                    // they are using guest account
                    navigationIntent.putExtra("account_type_key", "guest")
                } else if (user.accountType == "parent") {
                    // they are using parent account
                    navigationIntent.putExtra("account_type_key", "parent")
                } else if (user.accountType == "child") {
                    // they are using child account
                    navigationIntent.putExtra("account_type_key", "child")
                }
                startActivity(navigationIntent)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        toolbar = view.findViewById(R.id.toolbar)
        setHasOptionsMenu(true)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = view.findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // TODO: No exception handling if registration fails
            if (email.isNotBlank() && password.isNotBlank()) {
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