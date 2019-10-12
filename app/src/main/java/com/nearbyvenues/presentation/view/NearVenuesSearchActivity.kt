package com.nearbyvenues.presentation.view

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.chip.Chip
import com.nearbyvenues.R
import com.nearbyvenues.TheApplication
import com.nearbyvenues.model.domain.VenueType
import com.nearbyvenues.presentation.presenter.NearVenuesSearchPresenter
import kotlinx.android.synthetic.main.layout_near_venues_search.button_near_venues_search_find_me
import kotlinx.android.synthetic.main.layout_near_venues_search.chgr_venues_filter_chips
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_press_locate_me_hint
import kotlinx.android.synthetic.main.layout_near_venues_search.tv_your_location_text
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class NearVenuesSearchActivity : MvpAppCompatActivity(), NearVenuesSearchView {

    @InjectPresenter
    lateinit var presenter: NearVenuesSearchPresenter

    @ProvidePresenter
    fun providePresenter(): NearVenuesSearchPresenter {
        return TheApplication.getAppComponent().getNearVenuesSearchPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TheApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_near_venues_search)

        button_near_venues_search_find_me.setOnClickListener { onLocateMePressedWithPermissionCheck() }

    }

    override fun setCurrentLocation(location: Location) {
        tv_your_location_text.text = getString(R.string.near_venues_search_text_your_location_text).format(location.latitude, location.longitude)

    }

    override fun showPressLocateMeWarning(show: Boolean) {
        tv_press_locate_me_hint.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showTurnOnLocationDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showGrantPermissionsDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showGrantPermissionInSettingsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.near_venues_search_dialog_missing_permissions_title))
        dialogBuilder.setMessage(getString(R.string.near_venues_search_dialog_missing_permissions_text))
        dialogBuilder.setPositiveButton(R.string.dialog_ok_button, null)
        dialogBuilder.show()
    }

    override fun addChipForVenueType(venue: VenueType) {
        val chip = Chip(this)

        chip.isCheckable = true
        chip.isClickable = true
        chip.text = venue.name

        chgr_venues_filter_chips.addView(chip)
    }

    override fun enableFilterVenueChips(enable: Boolean) {

        chgr_venues_filter_chips.forEach { chip ->
            chip.isEnabled = enable
        }
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
        presenter.onLocationPermissionsDenied()
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
