package com.nearbyvenues.presentation.find_venues.view

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.paging.PagedList
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.chip.Chip
import com.nearbyvenues.R
import com.nearbyvenues.TheApplication
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import com.nearbyvenues.presentation.find_venues.view.recycler.NearbyVenuesRecyclerAdapter
import com.nearbyvenues.utils.RecyclerViewOnItemClickListener
import kotlinx.android.synthetic.main.layout_near_venues_search.button_near_venues_search_find_me
import kotlinx.android.synthetic.main.layout_near_venues_search.chgr_venues_filter_chips
import kotlinx.android.synthetic.main.layout_near_venues_search.pb_venues_search
import kotlinx.android.synthetic.main.layout_near_venues_search.rv_venues_search_list
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_location_error
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_press_locate_me_hint
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_venues_search_error
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_your_location_text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@ExperimentalCoroutinesApi
@FlowPreview
@RuntimePermissions
class NearVenuesSearchActivity : MvpAppCompatActivity(), NearVenuesSearchView {

    @InjectPresenter
    lateinit var presenter: NearbyVenuesSearchPresenter

    private var nearbyVenuesRecyclerAdapter = NearbyVenuesRecyclerAdapter()

    @ProvidePresenter
    fun providePresenter(): NearbyVenuesSearchPresenter {
        return TheApplication.getAppComponent().getNearVenuesSearchPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TheApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_near_venues_search)

        button_near_venues_search_find_me.setOnClickListener { onLocateMePressedWithPermissionCheck() }

        rv_venues_search_list.adapter = nearbyVenuesRecyclerAdapter
        rv_venues_search_list.setHasFixedSize(true)
        rv_venues_search_list.addOnItemTouchListener(RecyclerViewOnItemClickListener(this, rv_venues_search_list, object: RecyclerViewOnItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val item: Venue?  = nearbyVenuesRecyclerAdapter.currentList?.get(position)
                if (item != null) {
                    presenter.onVenueClicked(item)
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {
            }
        }))


        tv_venues_search_error.setOnClickListener { presenter.onErrorClicked()}
    }

    override fun setCurrentLocation(location: Coordinates) {
        tv_your_location_text.text = getString(R.string.near_venues_search_text_your_location_text).format(location.lat, location.lng)

    }

    override fun showPressLocateMeWarning(show: Boolean) {
        tv_press_locate_me_hint.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showGrantPermissionInSettingsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.near_venues_search_dialog_missing_permissions_title))
        dialogBuilder.setMessage(getString(R.string.near_venues_search_dialog_missing_permissions_text))
        dialogBuilder.setPositiveButton(R.string.dialog_ok_button, null)
        dialogBuilder.show()
    }

    override fun addChipsForVenues(venues: List<VenueType>) {
        venues.forEach { venue->
            val chip = Chip(this)

            chip.isCheckable = true
            chip.isClickable = true
            chip.text = getVenueNameForVenueType(venue)
            chip.tag = venue

            chip.setOnCheckedChangeListener { buttonView, isChecked -> onChipCheckedChanged() }

            chgr_venues_filter_chips.addView(chip)
        }

    }

    override fun setCheckedChipsForVenues(venues: List<VenueType>) {
        chgr_venues_filter_chips.forEach { view ->
            val chip = view as Chip

            if (venues.contains(chip.tag as VenueType)) {
                chip.isChecked = true
            }
        }
    }

    private fun onChipCheckedChanged() {
        val currentVenueTypes = mutableListOf<VenueType>()

        chgr_venues_filter_chips.forEach { view ->
            val chip = view as Chip

            if (chip.isChecked) {
                currentVenueTypes.add(chip.tag as VenueType)
            }
        }

        presenter.onVenueFilterChanged(currentVenueTypes)

    }

    override fun enableFilterVenueChips(enable: Boolean) {

        chgr_venues_filter_chips.forEach { chip ->
            chip.isEnabled = enable
        }
    }

    private fun getVenueNameForVenueType(venue: VenueType): String {
        return when(venue) {
            VenueType.BAR -> getString(R.string.near_venues_search_chip_bars)
            VenueType.RESTAURANT -> getString(R.string.near_venues_search_chip_restaurants)
            VenueType.CAFE -> getString(R.string.near_venues_search_chip_cafes)
        }
    }

    override fun showProgress(show: Boolean) {
        pb_venues_search.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNoConnectionError(show: Boolean) {
        tv_venues_search_error.setText(R.string.near_venues_search_error_no_network)
        tv_venues_search_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateVenueList(venues: PagedList<Venue>) {
        nearbyVenuesRecyclerAdapter.submitList(venues)
    }

    override fun showGeneralError(show: Boolean) {
        tv_venues_search_error.setText(R.string.near_venues_search_error_general)
        tv_venues_search_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showCannotLocateError(show: Boolean) {
        tv_location_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showVenueDetails(venue: Venue) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.near_venues_search_dialog_venue_details_title))
        val message = venue.name + "\n" +
                getString(R.string.near_venues_search_dialog_venue_details_rating,  venue.rating) + "\n" +
                if (venue.openNow != null) {
                    if (venue.openNow) {
                        getString(R.string.near_venues_search_dialog_venue_details_opened_now)
                    } else {
                        getString(R.string.near_venues_search_dialog_venue_details_closed_now)
                    }
                } else ""

        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton(R.string.dialog_ok_button, null)
        dialogBuilder.show()
    }

    override fun clearList() {
        nearbyVenuesRecyclerAdapter = NearbyVenuesRecyclerAdapter()
        rv_venues_search_list.adapter = nearbyVenuesRecyclerAdapter
        nearbyVenuesRecyclerAdapter.notifyDataSetChanged()
    }

    // Permission dispatcher handlers
    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)
    fun onLocateMePressed() {
        presenter.onLocateMePressed()
    }


    @OnShowRationale(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)
    fun onShowRationaleForLocationPermission(request: PermissionRequest) {
        presenter.onShowRationaleForLocationPermission(request)
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)
    fun onLocationPermissionsDenied() {
    }

    @OnNeverAskAgain(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)
    fun onLocationPermissionsNeverAsAgain() {
        presenter.onLocationPermissionsNeverAsAgain()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    // ^ Permission dispatcher handlers


}
