package com.youthconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.FragmentEventsBinding

// event admin fragment to display events and activities
class EventsAdminFragment : Fragment() {

    // create variable for firebase firestore
    private lateinit var firestore: FirebaseFirestore
    // create variable for FragmentEventsBinding
    private lateinit var bindingEvents: FragmentEventsBinding

    // create list of event model
    private var eventsList : MutableList<EventModel> = mutableListOf()
    // create object of the event adapter
    private lateinit var eventAdapter: EventAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingEvents = FragmentEventsBinding.inflate(inflater, container, false)

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // initialise event Adapter
        eventAdapter = EventAdminAdapter(eventsList, requireContext())

        // when admin clicks to create activity, navigate to Create Event Activity
        bindingEvents.btnCreateEvent.setOnClickListener{
            val i = Intent(context, CreateEventActivity::class.java)
            i.putExtra("EVENTID", "null")    //pass event.id
            i.putExtra("Mode", "")
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(i)
        }

        return bindingEvents.root   // return the root of the binding
    }

    // on view created will run after the events fragment has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // add all events to the eventsList
        firestore.collection("events").get().addOnSuccessListener { result ->
            for (doc in result) {
                var id : String = doc.getString("id").toString()
                var title : String = doc.getString("title").toString()
                var date : String = doc.getString("date").toString()
                var time : String = doc.getString("time").toString()
                var description : String = doc.getString("description").toString()
                eventsList.add(EventModel(id, title, date, time, description))
            }

            // apply the adapter to the recycler view
            bindingEvents.apply {
                rvEvent.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = eventAdapter
                }
            }
        }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error getting documents: ", exception)
            }

    }
}