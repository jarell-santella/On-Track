package com.android.on_track.ui.loginregister

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.ui.navigation.NavigationActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var navigationIntent: Intent

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        setHasOptionsMenu(true)

        val firstNameInput = view.findViewById<TextInputEditText>(R.id.first_name_input)
        val lastNameInput = view.findViewById<TextInputEditText>(R.id.last_name_input)
        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val registerButton = view.findViewById<MaterialButton>(R.id.register_button)
        val accountTypeDropdown = view.findViewById<MaterialAutoCompleteTextView>(R.id.account_type_dropdown)
        ArrayAdapter.createFromResource(requireContext(), R.array.account_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            accountTypeDropdown.setAdapter(adapter)
        }

        registerButton.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val accountType = accountTypeDropdown.text.toString()

            // TODO: No exception handling if registration fails
            if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                viewModel.register(email, password, firstName, lastName, accountType)
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