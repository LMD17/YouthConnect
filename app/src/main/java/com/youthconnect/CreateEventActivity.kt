package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.ActivityCreateEventBinding
import java.util.UUID

// activity to create an event
class CreateEventActivity : AppCompatActivity() {
    // create variable register binding
    private lateinit var binding: ActivityCreateEventBinding

    // create variable firebase firestore
    private lateinit var firestore: FirebaseFirestore

    // create variable DocumentReference
    private lateinit var docreference: DocumentReference

    // create variable eventId
    private lateinit var eventId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize binding
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)    // set content view

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        //Declare variable and initialise
        var isEditMode: Boolean = false


        //if Mode 'E' then user selected to edit data and we must update data in the database
        if (intent != null && intent.getStringExtra("Mode") == "E") {
            //set edit mode to true
            isEditMode = true
            // change the title and button text
            binding.tvEvent.text = "Update Event"
            binding.btnSubmit.text = "Update"
            // make delete button and ID edit text visible when viewing and editing an event
            binding.btnDelete.visibility = View.VISIBLE
            binding.etId.visibility = View.VISIBLE  // the id is not editable but it should be visible

            eventId = intent.getStringExtra("EVENTID").toString()   // get the event_id fro the intent
            Log.d("EVENT", "Event ID: ${eventId}")

            // get the event's account document from the collection using the eventId
            val docreference: DocumentReference = firestore.collection("events").document(eventId)
            // update the profile fields whenever there is a change to the user data
            docreference.get()
                .addOnSuccessListener { document ->
                if (document.data != null) {
                    // set the text of the edit text fields with the user details
                    binding.etId.setText(document.getString("id"))
                    binding.etTitle.setText(document.getString("title"))
                    binding.etDate.setText(document.getString("date"))
                    binding.etTime.setText(document.getString("time"))
                    binding.etActivities.setText(document.getString("activities"))
                } else {
                    Log.e("Event", "Event Retrieval Failed")
                }
            }
        } else{
            isEditMode = false  //set edit mode to false
            // change the title and button text
            binding.tvEvent.text = "Create Event"
            binding.btnSubmit.text = "Create"
            // make delete button and ID edit text invisible when creating an event
            binding.btnDelete.visibility = View.INVISIBLE
            binding.etId.visibility = View.INVISIBLE
        }

        // set listener for when user clicks on submit button
        binding.btnSubmit.setOnClickListener {
            // get user input
            val title = binding.etTitle.text.toString()
            val date = binding.etDate.text.toString()
            val time = binding.etTime.text.toString()
            val activities = binding.etActivities.text.toString()

            // validate inputs to check that user has input data
            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && activities.isNotEmpty()) {


                // check if edit mode is activate (are we editing a ticket)
                if (isEditMode) {
                    // initialize MutableMap with event data
                    val event: MutableMap<String, Any> = mutableMapOf()
                    event["id"] = binding.etId.text.toString()  // when editing event we use existing event id
                    event["title"] = title
                    event["date"] = date
                    event["time"] = time
                    event["activities"] = activities
                    // add event
                    // get the event's account document from the collection using the event id
                    val docreference: DocumentReference = firestore.collection("events")
                        .document(eventId)
                    docreference.update(event).addOnSuccessListener {
                        // toast the success to the user when the event is updated
                        Toast.makeText(this, "Event Updated", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(
                                this,
                                AdminActivity::class.java
                            )
                        )  // navigate back to events fragment
                    }.addOnFailureListener{
                        // toast the success to the user when the event is updated
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show()
                    }
                } else { // else are we creating a new ticket
                    // initialize MutableMap with event data
                    val id = UUID.randomUUID().toString()
                    val event: MutableMap<String, Any> = mutableMapOf()
                    event["id"] = id  // generate random id for event creation
                    event["title"] = title
                    event["date"] = date
                    event["time"] = time
                    event["activities"] = activities
                    // initialize document reference
                    docreference = firestore.collection("events").document(id)
                    // add event
                    docreference.set(event).addOnSuccessListener {
                        // toast the success to the user when the event is created
                        Toast.makeText(this, "Event Created", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(
                                this,
                                AdminActivity::class.java
                            )
                        )  // navigate back to admin activity
                    }.addOnFailureListener{
                        // toast the success to the user when the event is updated
                        Toast.makeText(this, "Create Event Failed", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // toast the error to the user if create user fails
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
            }
        }

        // when admin clicks to delete button, delete the event based on eventId
        binding.btnDelete.setOnClickListener{
            // initialize document reference
            docreference = firestore.collection("events").document(eventId)
            docreference.delete()   // delete the event
            // toast the success to the user when the event is deleted
            Toast.makeText(this, "Event Deleted", Toast.LENGTH_LONG).show()
            startActivity(
                Intent(
                    this,
                    AdminActivity::class.java
                )
            )  // navigate back to admin activity
        }
    }
}