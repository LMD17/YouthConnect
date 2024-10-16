package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.ActivityLoginBinding
import com.youthconnect.databinding.ActivityRegisterBinding

// login activity to handle user authentication. Also direct user to correct activity if they are an admin or affiliate
class LoginActivity : AppCompatActivity() {

    // create variable for register binding
    private lateinit var binding: ActivityLoginBinding

    // create variable for firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    // create instance of firebase firestore
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // initialize binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // initialize firestore
        firestore = FirebaseFirestore.getInstance()



        // set on click listener for already have an account text view
        binding.tvGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // set listener for when user clicks on submit button
        binding.btnSubmit.setOnClickListener {
            // get user input
            val email = binding.etUserEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // validate inputs to check that user has input data
            if (email.isNotEmpty() && password.isNotEmpty()) {

                // create user using firebase auth
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {  // if login is successful then we go to Affiliate or Admin Activities (depending on the user)
                        val uid = firebaseAuth.currentUser!!.uid
                        // get the user's account document from the collection using the userId
                        val docreference: DocumentReference = firestore.collection("users").document(uid)

                        // get user role
                        docreference.get()
                            .addOnSuccessListener { document ->
                                if (document.data != null) {
                                    // if the role is admin then take user to admin activity
                                    var role = document.getString("role")
                                    if (role == "admin"){
                                        Toast.makeText(this, "Welcome Admin", Toast.LENGTH_LONG).show()
                                        // navigate to admin activity
                                        val intent = Intent(this, AdminActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{  // else user is affiliate and must be taken to affiliate activity
                                        Toast.makeText(this, "Welcome Affiliate", Toast.LENGTH_LONG).show()
                                        // navigate to affiliate activity
                                        val intent = Intent(this, AffiliateActivity::class.java)
                                        startActivity(intent)
                                    }
                                } else {
                                    Log.d("TAG", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "get failed with ", exception)
                            }
                    } else {
                        // toast the error to the user if create user fails
                        Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // toast to user that all fields require input before submission
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_LONG).show()
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        if(firebaseAuth.currentUser != null){
//            val uid = firebaseAuth.currentUser?.uid
//            if(uid != ""){
//                val intent = Intent(this, AdminActivity::class.java)
//                startActivity(intent)
//            }
//
//        }
//    }
}