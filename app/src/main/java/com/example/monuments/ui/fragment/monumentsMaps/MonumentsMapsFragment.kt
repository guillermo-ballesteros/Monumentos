package com.example.monuments.ui.fragment.monumentsMaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.monuments.R
import com.example.monuments.databinding.FragmentMonumentsMapsBinding
import com.example.monuments.domain.MonumentBO
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.adapter.CustomInfoWindowAdapter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MonumentsMapsFragment : Fragment(), OnMapReadyCallback{

    private val viewModel by lazy { ViewModelProvider(this).get(MonumentsMapViewModel::class.java) }

    private val locationPermissionRequestCode = 1

    private var map: GoogleMap? = null

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    private var monumentsMapViewBinding: FragmentMonumentsMapsBinding? = null

    private var lastLocation: Location? = null

    private val spinnerListener = object: AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val monuments: List<MonumentBO> = viewModel.getListFiltered(monumentsMapViewBinding?.mapSpinnerFilterOptions?.getItemAtPosition(position).toString(), lastLocation)
                addMarkers(monuments)

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

    }

    private val infoWindowClickListener =
        GoogleMap.OnInfoWindowClickListener { p0 ->
            val monument = p0.title?.let {
                viewModel.getMonument(it)
            }
            if (monument != null) {
                val action = MonumentsMapsFragmentDirections.actionMonumentsMapsToDetail(monument.id)
                NavHostFragment.findNavController(this@MonumentsMapsFragment).navigate(action)
            }
        }

    private val mapObserver = Observer<List<MonumentBO>> {
        addMarkers(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        monumentsMapViewBinding = FragmentMonumentsMapsBinding.inflate(inflater, container, false)
        return monumentsMapViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        monumentsMapViewBinding?.mapBtnBackBtn?.setOnClickListener {
            val action = MonumentsMapsFragmentDirections.actionMonumentsMapsToAllMonuments()
            NavHostFragment.findNavController(this@MonumentsMapsFragment).navigate(action)
        }

        createSpinnerOptions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == locationPermissionRequestCode) {
            if(grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activateLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun activateLocation() {
        map?.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
            }
        }
        val mapView: View? = view?.findViewById(R.id.map)
        val locationButton = (mapView?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
        locationButton.visibility = View.GONE
        monumentsMapViewBinding?.mapBtnLocation?.setOnClickListener {
            locationButton.callOnClick()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isMapToolbarEnabled = false
        setUpMap()
        map?.setInfoWindowAdapter( activity?.let { it1 -> CustomInfoWindowAdapter(it1) } )
        map?.setOnInfoWindowClickListener(infoWindowClickListener)
        viewModel.getMonuments().observe(viewLifecycleOwner, mapObserver)

        monumentsMapViewBinding?.mapSpinnerFilterOptions?.onItemSelectedListener = spinnerListener

    }

    private fun setUpMap() {
        if( ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode )
                return
        } else {
            activateLocation()
        }
    }

    private fun addMarkers(monuments: List<MonumentBO>?) {
        map?.clear()
        if (monuments != null) {
            for (monument in monuments) {
                val location = LatLng(monument.location.latitude,
                    monument.location.longitude)
                map?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                ).anchor(0.0f, 1.0f).position(location)
                    .title(monument.name))
            }
        }
    }

    private fun createSpinnerOptions() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.detail_map_filter,
            android.R.layout.simple_spinner_dropdown_item
        )
        monumentsMapViewBinding?.mapSpinnerFilterOptions?.adapter = adapter
    }

}

