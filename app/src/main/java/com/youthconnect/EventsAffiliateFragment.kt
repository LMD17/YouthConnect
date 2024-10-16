package com.youthconnect

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.youthconnect.databinding.FragmentEventsAffiliateBinding

// event affiliate fragment to display events and activities
class EventsAffiliateFragment : Fragment() {

    // create instance of firebase firestore
    private lateinit var firestore: FirebaseFirestore

    private lateinit var bindingAffiliateEvents: FragmentEventsAffiliateBinding

    // create list of event model
    private var eventsList : MutableList<EventModel> = mutableListOf()
    // create object of the event adapter
    private lateinit var eventAdapter: EventAffiliateAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingAffiliateEvents = FragmentEventsAffiliateBinding.inflate(inflater, container, false)

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()

        // initialise event Adapter
        eventAdapter = EventAffiliateAdapter(eventsList, requireContext())

        return bindingAffiliateEvents.root
    }

    // on resume will run everytime we go to the affiliate events fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // add all events to the eventsList
        firestore.collection("events").get().addOnSuccessListener { result ->
            for (doc in result) {
                var id : String = doc.getString("id").toString()
                var title : String = doc.getString("title").toString()
                var date : String = doc.getString("date").toString()
                var time : String = doc.getString("time").toString()
                var activities : String = doc.getString("activities").toString()
                eventsList.add(EventModel(id, title, date, time, activities))
            }
            // apply the adapter to the recycler view
            bindingAffiliateEvents.apply {
                rvAffiliateEvent.apply {
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