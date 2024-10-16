package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Profile
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.youthconnect.databinding.ActivityAdminBinding
import com.youthconnect.databinding.ActivityAffiliateBinding
import com.youthconnect.databinding.ActivityLoginBinding
import com.youthconnect.databinding.FragmentProfileBinding

class AffiliateActivity : AppCompatActivity() {

    // create variable for firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    // create instance of firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // create variable for register binding
    private lateinit var binding: ActivityAffiliateBinding
    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var frameLayout: FrameLayout
    private lateinit var tabLayout: TabLayout
    lateinit var fragment: Fragment

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // initialize activity admin binding
        binding = ActivityAffiliateBinding.inflate(layoutInflater)
        setContentView(binding.root)    // set content view
        // initialize fragment profile binding
        bindingProfile = FragmentProfileBinding.inflate(layoutInflater)


        frameLayout = binding.frameLayout
        tabLayout = binding.tabLayout

        // get user id
        userId = firebaseAuth.currentUser!!.uid


        // load first fragment by default
        supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, ProfileFragment())
            .addToBackStack(null)
            .commit()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab){

                // identify which tab is selected
                when (tab.position){
                    0 -> {
                        fragment = ProfileFragment()
                    }
                    1 -> {
                        fragment = EventsAffiliateFragment()
                    }
                }

                // change to selected fragment
                supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
}