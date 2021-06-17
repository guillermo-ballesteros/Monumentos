package com.example.monuments.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.monuments.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import java.lang.ref.WeakReference

class CustomInfoWindowAdapter(context: Context): GoogleMap.InfoWindowAdapter {
    private val contextWR = WeakReference(context)
    @SuppressLint("InflateParams")
    override fun getInfoWindow(p0: Marker): View? {
        val context = contextWR.get()
        if (context != null) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.info_window, null)
            view.layoutParams = ConstraintLayout.LayoutParams(250,150)
            rendowWindowText(p0, view)
            return view
        }
        return null
    }
    private fun rendowWindowText(marker: Marker, view: View) {
        val layout: ConstraintLayout = view.findViewById(R.id.info_window__layout__main_layout)
        val title: TextView = view.findViewById(R.id.info_window__label__title)
        val address: TextView = view.findViewById(R.id.info_window__label__location)
        title.text = marker.title
        address.text = getAddress(marker.position.latitude, marker.position.longitude, view.context)
    }
    override fun getInfoContents(p0: Marker): View? {
        return null
    }
    private fun getAddress(latitude: Double, longitude: Double, context: Context): String {
        val geocoder = Geocoder(context)
        val list = geocoder.getFromLocation(latitude, longitude, 1)
        return list[0].getAddressLine(0)

    }
}