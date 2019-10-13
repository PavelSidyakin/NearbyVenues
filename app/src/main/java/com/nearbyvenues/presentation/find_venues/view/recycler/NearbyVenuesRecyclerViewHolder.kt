package com.nearbyvenues.presentation.find_venues.view.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nearbyvenues.R
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.Venue
import kotlinx.android.synthetic.main.recycler_item_venues_search.view.tv_recycler_item_distance
import kotlinx.android.synthetic.main.recycler_item_venues_search.view.tv_recycler_item_name
import kotlinx.android.synthetic.main.recycler_item_venues_search.view.tv_recycler_item_venue_types

class NearbyVenuesRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    fun bind(venue: Venue?) {
        if (venue != null) {

            itemView.tv_recycler_item_venue_types.text = venue.types.joinToString("\n") { venueType ->
                itemView.tv_recycler_item_venue_types.context.getString(venueTypeNameResId(venueType))
            }

            itemView.tv_recycler_item_name.text = venue.name
            itemView.tv_recycler_item_distance.text =
                itemView.tv_recycler_item_distance.context.getString(R.string.near_venues_search_list_item_distance, venue.distance)
        }
    }

    private fun venueTypeNameResId(venueType: VenueType): Int {
        return mapVenueTypeAndResId[venueType] ?: throw IllegalArgumentException("Unknown VenueType: $venueType")
    }

    companion object {
        fun create(parent: ViewGroup): NearbyVenuesRecyclerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_venues_search, parent, false)
            return NearbyVenuesRecyclerViewHolder(view)
        }

        private val mapVenueTypeAndResId= mapOf(
            VenueType.BAR to R.string.near_venues_search_list_item_bar,
            VenueType.RESTAURANT to R.string.near_venues_search_list_item_restaurant,
            VenueType.CAFE to R.string.near_venues_search_list_item_cafe
        )

    }
}

