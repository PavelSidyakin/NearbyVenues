package com.nearbyvenues.presentation.find_venues.view.recycler

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nearbyvenues.model.domain.Venue

class NearbyVenuesRecyclerAdapter : PagedListAdapter<Venue, RecyclerView.ViewHolder>(venueDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return NearbyVenuesRecyclerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NearbyVenuesRecyclerViewHolder).bind(getItem(position))
    }

    companion object {
        val venueDiffCallback = object : DiffUtil.ItemCallback<Venue>() {
            override fun areItemsTheSame(oldItem: Venue, newItem: Venue): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Venue, newItem: Venue): Boolean {
                return oldItem == newItem
            }
        }
    }
}