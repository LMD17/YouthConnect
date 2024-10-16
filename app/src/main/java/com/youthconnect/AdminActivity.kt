package com.youthconnect

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.ActivityAdminBinding

// admin activity
class AdminActivity : AppCompatActivity() {

    // create variable for firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
    // create variable for firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // create variables
    private lateinit var binding: ActivityAdminBinding
    private lateinit var frameLayout: FrameLayout
    private lateinit var tabLayout: TabLayout
    lateinit var fragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // initialize activity admin binding
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get frame layout and tablayout
        frameLayout = binding.frameLayout
        tabLayout = binding.tabLayout

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
                        fragment = UsersFragment()
                    }
                    2 -> {
                        fragment = EventsAdminFragment()
                    }
                }

                // change to selected fragment
                supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

    }



}