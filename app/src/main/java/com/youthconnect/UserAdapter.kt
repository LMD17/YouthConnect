package com.youthconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.UserRowBinding

// User adapter for admin
class UserAdapter (private val items : MutableList<UserModel>, internal var context : Context)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    // create variable for User Row
    private lateinit var binding: UserRowBinding

    // create instance of firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // Create the view handler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        binding = UserRowBinding.inflate(inflater, parent, false)
        // initialize firestore
        firestore = FirebaseFirestore.getInstance()
        return ViewHolder(binding)
    }

    // bind the view handler
    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
        var user = items[position]
        //This button on click listener re-directs the user to the ticket detail page
        holder.buttonRoleChange.setOnClickListener{

            // make the user role the opposite to what it was
            if (user.role == "affiliate") user.role = "admin"
            else user.role = "affiliate"

            // get the user's account document from the collection using the userId
            val docreference: DocumentReference = firestore.collection("users").document(user.id)
            docreference.update("role", user.role)  // update the user role
        }
    }

    // get items count
    override fun getItemCount(): Int {
        return items.size
    }

    // inner view holder class
    inner class ViewHolder(itemView : UserRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        //link variables to button
        var buttonRoleChange = itemView.btnRoleChange

        fun bind(item : UserModel){
            binding.apply {
                // apply text to text view fields
                tvUserId.text = item.id.toString()
                tvEmail.text = item.email.toString()
                tvName.text = item.name.toString()
                tvSurname.text = item.surname.toString()
                tvRole.text = item.role.toString()
            }
        }
    }
}