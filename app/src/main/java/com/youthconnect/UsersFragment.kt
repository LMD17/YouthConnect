package com.youthconnect

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.FragmentUsersBinding

// user fragment displays all the users of the app
class UsersFragment : Fragment() {

    // create variable for firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // create variable for FragmentUsersBinding
    private lateinit var bindingUsers: FragmentUsersBinding

    // create list of event model
    private var usersList : MutableList<UserModel> = mutableListOf()
    // create object of the event adapter
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingUsers = FragmentUsersBinding.inflate(inflater, container, false)

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // initialise event Adapter
        userAdapter = UserAdapter(usersList, requireContext())

        // Inflate the layout for this fragment
        return bindingUsers.root
    }


    // on view created will run after the events fragment has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // add all events to the eventsList
        firestore.collection("users").get().addOnSuccessListener { result ->
            for (doc in result) {
                var id: String = doc.getString("id").toString()
                var email: String = doc.getString("email").toString()
                var name: String = doc.getString("name").toString()
                var surname: String = doc.getString("surname").toString()
                var role: String = doc.getString("role").toString()
                usersList.add(UserModel(id, email, name, surname, role))
            }

            // apply the adapter to the recycler view
            bindingUsers.apply {
                rvUser.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = userAdapter
                }
            }
        }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error getting documents: ", exception)
            }
    }
}