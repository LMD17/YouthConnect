package com.youthconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youthconnect.databinding.EventAffiliateRowBinding

// Event adapter for affiliate
class EventAffiliateAdapter (private val items : MutableList<EventModel>, internal var context : Context)
    : RecyclerView.Adapter<EventAffiliateAdapter.ViewHolder>(){

    private lateinit var binding: EventAffiliateRowBinding

    // Create the view handler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAffiliateAdapter.ViewHolder {
        val inflater =  LayoutInflater.from(parent.context)
        binding = EventAffiliateRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // bind the view handler
    override fun onBindViewHolder(holder: EventAffiliateAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // get items count
    override fun getItemCount(): Int {
        return items.size
    }

    // inner view holder class
    inner class ViewHolder(itemView : EventAffiliateRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        // bind function to bind items to text fields
        fun bind(item : EventModel){
            binding.apply {
                // apply text to text view fields
                tvEventId.text = item.id.toString()
                tvTitle.text = item.title.toString()
                tvDate.text = item.date.toString()
                tvTime.text = item.time.toString()
                tvActivities.text = item.activities.toString()
            }
        }
    }
}