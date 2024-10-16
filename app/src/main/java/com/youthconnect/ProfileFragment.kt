package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.youthconnect.databinding.FragmentProfileBinding

// profile fragment to display and handle profile details and changes.
class ProfileFragment : Fragment() {

    private lateinit var userId: String
    // create variable for firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    // create variable for firebase firestore
    private lateinit var firestore: FirebaseFirestore
    // create variable for bindingProfile
    private lateinit var bindingProfile: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflate the layout for this fragment using view binding
        bindingProfile = FragmentProfileBinding.inflate(inflater, container, false)
        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // get user id
        userId = firebaseAuth.currentUser!!.uid

        // get the user's account document from the collection using the userId
        val docreference: DocumentReference = firestore.collection("users").document(userId)

        // get edit text fields
        var name: EditText = bindingProfile.etName
        var surname: EditText = bindingProfile.etSurname
        var email: EditText = bindingProfile.etUserEmail
        var password: EditText = bindingProfile.etPassword
        var role: EditText = bindingProfile.etRole

        // on click listener for update button
        bindingProfile.btnUpdate.setOnClickListener {

            // create user map from user details in edit text fields
            val user: MutableMap<String, Any> = mutableMapOf()
            user["name"] = name.text.toString()
            user["surname"] = surname.text.toString()
            user["email"] = email.text.toString()
            user["password"] = password.text.toString()
            user["role"] = role.text.toString()

            // get current signed in user
            val userAuth = FirebaseAuth.getInstance().currentUser

            // get user details
            docreference.get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        // Get auth credentials from the user for re-authentication
                        val credential = EmailAuthProvider
                            .getCredential(document.get("email").toString(), document.get("password").toString()) // Current Login Credentials

                        // if the email is different then update email and send email verification
                        if(document.get("email") != email.text.toString()){
                            // Re-authenticate user to with their sign-in credentials
                            userAuth?.reauthenticate(credential)?.addOnCompleteListener(object : OnCompleteListener<Void?> {
                                override fun onComplete(p0: Task<Void?>) {
                                    val userAuth = FirebaseAuth.getInstance().currentUser

                                    Log.d("TAG", "User re-authenticated.")
                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\

                                    // update user email
                                    if (userAuth != null) {
                                        userAuth.updateEmail(email.text.toString())
                                            .addOnCompleteListener(object :
                                                OnCompleteListener<Void?> {
                                                override fun onComplete(p0: Task<Void?>) {
                                                    if (p0.isSuccessful) {
                                                        Log.d("TAG", "UPDATED EMAIL.")
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Updated EMAIL",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        userAuth.sendEmailVerification()
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        "Verification email sent",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                } else {
                                                                    Log.e(
                                                                        "TAG",
                                                                        "Error: ${task.exception}."
                                                                    )
                                                                }
                                                            }
                                                    } else {
                                                        Log.e(
                                                            "TAG",
                                                            "Failed to update email: ${p0.exception?.message}"
                                                        )
                                                    }
                                                }
                                            })
                                    }

                                    if (document.get("password") != password.text.toString()) {
                                        // update user password
                                        if (userAuth != null) {
                                            userAuth.updatePassword(password.text.toString())
                                                .addOnCompleteListener(object :
                                                    OnCompleteListener<Void?> {
                                                    override fun onComplete(p0: Task<Void?>) {
                                                        if (p0.isSuccessful) {
                                                            Log.d("TAG", "Updated password.")
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Updated PASSWORD",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                })
                                        }

                                    }
                                }
                            })
                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }


            // update the user details
            docreference.update(user)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Updated SUCCESSFULLY",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener {
                    // failed to update
                    Toast.makeText(requireContext(), "Update FAILED", Toast.LENGTH_LONG).show()
                }

        }

        // on click listener for delete button
        bindingProfile.btnDelete.setOnClickListener{
            // delete the users account (the document in this case)
            docreference.delete()
            firebaseAuth.currentUser!!.delete().addOnCompleteListener { // delete users auth details
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Account DELETED", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)     // navigate user to login page
                }
                else{
                    Toast.makeText(requireContext(), "Delete FAILED", Toast.LENGTH_LONG).show()
                }
            }
        }

        // on click listener for logout button
        bindingProfile.btnLogout.setOnClickListener{
            // sign the user out of their account and send them back to the login page
            firebaseAuth.signOut()  // sign user out
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)     // navigate user to login page
            Toast.makeText(requireContext(), "Logout Successful", Toast.LENGTH_LONG).show() // toast that logout is successful
            this.activity?.finish() // close the parent activity

        }

        // Inflate the layout for this fragment
        return bindingProfile.root
    }

    // on resume will run everytime we go to the profile fragment
    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({

            // get the user's account document from the collection using the userId
            val docreference: DocumentReference = firestore.collection("users").document(userId)
                    // update the profile fields whenever there is a change to the user data
            docreference.addSnapshotListener(requireActivity(), object : EventListener<DocumentSnapshot> {
                override fun onEvent(
                    documentSnapshot: DocumentSnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // set the text of the edit text fields with the user details
                        bindingProfile.etName.setText(documentSnapshot.getString("name"))
                        bindingProfile.etSurname.setText(documentSnapshot.getString("surname"))
                        bindingProfile.etUserEmail.setText(documentSnapshot.getString("email"))
                        bindingProfile.etPassword.setText("**********") // password is not stored in database for security reasons. User can however type a new password here to change their password
                        bindingProfile.etRole.setText(documentSnapshot.getString("role"))

                    } else {
                        Log.e("Profile", "Account Retrieval Failed")
                    }
                }
            })
        }, 500)

    }
}