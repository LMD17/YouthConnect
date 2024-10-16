package com.youthconnect

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youthconnect.databinding.EventRowBinding

// Event adapter for admin
class EventAdminAdapter (private val items : MutableList<EventModel>, internal var context : Context)
    : RecyclerView.Adapter<EventAdminAdapter.ViewHolder>(){

    private lateinit var binding: EventRowBinding

    // Create the view handler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdminAdapter.ViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        binding = EventRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // bind the view handler
    override fun onBindViewHolder(holder: EventAdminAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
        val event = items[position]
        //This button on click listener re-directs the user to the ticket detail page
        holder.buttonView.setOnClickListener{
            val i = Intent(context, CreateEventActivity::class.java)
            i.putExtra("EVENTID", event.id)    //pass event.id
            i.putExtra("Mode", "E")    //pass event.id
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }

    }

    // get items count
    override fun getItemCount(): Int {
        return items.size
    }

    // inner view holder class
    inner class ViewHolder(itemView : EventRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        //link variables to button
        var buttonView = itemView.btnView

        // bind function to bind items to text fields
        fun bind(item : EventModel){
            binding.apply {
                // apply text to text view fields
                tvEventId.text = item.id.toString()
                tvTitle.text = item.title.toString()
                tvDate.text = item.date.toString()
                tvTime.text = item.time.toString()
            }
        }
    }
}