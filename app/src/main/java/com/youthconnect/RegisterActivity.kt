package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.ActivityRegisterBinding

// register activity to handle user registration. All users are created as affiliates by default.
class RegisterActivity : AppCompatActivity() {

    // create variable register binding
    private lateinit var binding: ActivityRegisterBinding

    // create variable firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // create variable firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // create variable DocumentReference
    private lateinit var docreference: DocumentReference

    // create variable userId
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)    // set content view

        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // set on click listener for already have an account text view
        binding.tvGoLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // set listener for when user clicks on submit button
        binding.btnSubmit.setOnClickListener {
            // get user input
            val email = binding.etUserEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val surname = binding.etSurname.text.toString()

            // validate inputs to check that user has input data
            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty()) {

                // create user using firebase auth
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userId = firebaseAuth.currentUser!!.uid // get auto generated user id

                        // initialize document reference
                        docreference = firestore.collection("users").document(userId)


                        val user: MutableMap<String, Any> = mutableMapOf()
                        user["id"] = userId
                        user["name"] = name
                        user["surname"] = surname
                        user["email"] = email
                        user["role"] = "affiliate"  // users are all set to affiliate by default

                        // add user
                        docreference.set(user).addOnSuccessListener {
                            // toast the success to the user when their account is created
                            Toast.makeText(this, "User Created", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                    } else {
                        // toast the error to the user if create user fails
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // toast to user that all fields require input before submission
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_LONG).show()
            }
        }
    }
}