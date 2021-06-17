package com.example.monuments.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.monuments.R
import com.example.monuments.databinding.DialogMonumentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MonumentLocationDialogFragment: DialogFragment(), OnMapReadyCallback {
    private val mapViewKey = "MapViewBundleKey"

    private val location by lazy { LatLng(arguments?.getDouble(KEY_LATITUDE)?: 1.0, arguments?.getDouble(
        KEY_LONGITUDE)?: 1.0) }

    private val name by lazy { arguments?.getString(KEY_NAME) }

    private var dialogViewBinding: DialogMonumentLocationBinding? = null

    companion object {
        private const val KEY_LATITUDE = "key_latitude"
        private const val KEY_LONGITUDE = "key_longitude"
        private const val KEY_NAME = "key_name"
        fun newInstance(location: LatLng, name: String): MonumentLocationDialogFragment {
            val instance = MonumentLocationDialogFragment()
            val auxBundle = Bundle().apply {
                putDouble(KEY_LATITUDE, location.latitude)
                putDouble(KEY_LONGITUDE, location.longitude)
                putString(KEY_NAME, name)
            }
            instance.arguments = auxBundle
            return instance
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogViewBinding = DialogMonumentLocationBinding.inflate(LayoutInflater.from(context))
        return createDialog(savedInstanceState)
    }

    private fun createDialog(savedInstanceState: Bundle?): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(dialogViewBinding?.root)
        var mapBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(mapViewKey)
        }
        dialogViewBinding?.locationMapMonumentLocation?.onCreate(mapBundle)
        dialogViewBinding?.locationMapMonumentLocation?.getMapAsync(this)

        return builder.create()
    }

    override fun onMapReady(p0: GoogleMap) {
        p0.addMarker(
            MarkerOptions().position(location)
                .title(name))
        p0.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.location__map__monument_location) as SupportMapFragment?
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    override fun onStart() {
        super.onStart()
        dialogViewBinding?.locationMapMonumentLocation?.onStart()
    }

    override fun onStop() {
        super.onStop()
        dialogViewBinding?.locationMapMonumentLocation?.onStop()
    }

    override fun onPause() {
        super.onPause()
        dialogViewBinding?.locationMapMonumentLocation?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogViewBinding?.locationMapMonumentLocation?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        dialogViewBinding?.locationMapMonumentLocation?.onLowMemory()
    }



}