package com.example.monuments.ui.fragment.addMonuments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monuments.R
import com.example.monuments.databinding.FragmentAddMonumentsBinding
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.activity.MainActivity
import com.example.monuments.ui.adapter.ImagesAdapter
import com.example.monuments.ui.fragment.myMonuments.MyMonumentsFragmentDirections
import com.example.monuments.utils.Constants
import com.example.monuments.utils.KeyboardUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class AddMonumentsFragment : Fragment(), OnMapReadyCallback{

    private val viewModel by lazy { ViewModelProvider(this).get(AddMonumentsViewModel::class.java) }

    private var map: GoogleMap? = null

    private val registerActivityResult =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image: Uri? = result.data?.data
            viewModel.addImage(image)
        }
    }

    private val imagesListener = object : ImagesAdapter.ImagesListener {
        override fun onDeleteClick(position: Int) {
            viewModel.deleteImage(position)
        }

        override fun onAddClick() {
            loadImage()
        }

    }

    private val searchListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            val location: String = addMonumentsViewBinding?.addMonumentSearchSearchLocation?.query.toString()
            if (location.isNotBlank()) {
                val geocoder = Geocoder(requireActivity())
                try {
                    val address = geocoder.getFromLocationName(location, 1)
                    if (address.isEmpty()) {
                        view?.let { Snackbar.make(it, "This address doesn't exist", Snackbar.LENGTH_LONG).show() }

                    } else {
                        val latLng = LatLng(address[0].latitude, address[0].longitude)
                        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20F))
                    }

                } catch (e: IOException) {
                    Log.d("ERROR", e.toString())
                }
            }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }

    }

    private val progressBarObserver = Observer<Boolean> {
        addMonumentsViewBinding?.addMonumentProgressProgressBar?.changeVisible(it)
    }

    private val imagesObserver = Observer<List<Uri>> {
        adapter.submitList(it)

    }

    private val resultObserver = Observer<Int> {
        when(it) {
            Constants.SUCCESS_CODE -> {
                val action = AddMonumentsFragmentDirections.actionAddMonumentsFragmentToMyMonuments()
                NavHostFragment.findNavController(this).navigate(action)
            }
            Constants.NULL_DATA_CODE -> {
                view?.let { view ->
                    if (viewModel.images.value?.size == 0) {
                        Snackbar.make(view, getString(R.string.error_null_values), Snackbar.LENGTH_SHORT).show()
                    }
                }
                if (addMonumentsViewBinding?.addMonumentInputCity?.text.toString() == "") {
                    addMonumentsViewBinding?.addMonumentInputCity?.error = getString(R.string.error_null_values)
                }
                if (addMonumentsViewBinding?.addMonumentInputName?.text.toString() == "") {
                    addMonumentsViewBinding?.addMonumentInputName?.error = getString(R.string.error_null_values)
                }
                if (addMonumentsViewBinding?.addMonumentInputDescription?.text.toString() == "") {
                    addMonumentsViewBinding?.addMonumentInputDescription?.error = getString(R.string.error_null_values)
                }
            }
        }
    }

    private var addMonumentsViewBinding: FragmentAddMonumentsBinding? = null

    private val adapter = ImagesAdapter(imagesListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        addMonumentsViewBinding = FragmentAddMonumentsBinding.inflate(inflater, container, false)
        return addMonumentsViewBinding?.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        addMonumentsViewBinding?.addMonumentListImageList?.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        addMonumentsViewBinding?.addMonumentListImageList?.adapter = adapter

        viewModel.images.observe(viewLifecycleOwner,imagesObserver )

        viewModel.result.observe(viewLifecycleOwner, resultObserver)

        viewModel.progressBar.observe(viewLifecycleOwner, progressBarObserver)

        addMonumentsViewBinding?.addMonumentBtnAddMonument?.setOnClickListener {
            val center: LatLng? = map?.cameraPosition?.target
            if (center != null) {
                viewModel.addMonument(
                    addMonumentsViewBinding?.addMonumentInputName?.text.toString(),
                    addMonumentsViewBinding?.addMonumentInputCity?.text.toString(),
                    addMonumentsViewBinding?.addMonumentInputDescription?.text.toString(),
                    addMonumentsViewBinding?.addMonumentInputUrlExtra?.text.toString(),
                    center)
            }
        }

        addMonumentsViewBinding?.addMonumentLayoutNestedScrollLayout?.setOnTouchListener { v, event ->
            val activity: MainActivity = activity as MainActivity
            KeyboardUtils.closeKeyboard(activity)
            false
        }

        addMonumentsViewBinding?.addMonumentSearchSearchLocation?.setOnQueryTextListener(searchListener)

        fixMapScrolling()
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map?.uiSettings?.isMapToolbarEnabled = false
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/"
//        startActivityForResult(Intent.createChooser(intent, "Select the Application"), 10)

        registerActivityResult?.launch(intent)
    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 10) {
//            val image: Uri? = data?.data
//            viewModel.addImage(image)
//        }
//    }

    @SuppressLint("ClickableViewAccessibility")
    private fun fixMapScrolling() {
        addMonumentsViewBinding?.addMonumentImgFixScrollImage?.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    addMonumentsViewBinding?.addMonumentLayoutNestedScrollLayout?.requestDisallowInterceptTouchEvent(true)
                    false
                }
                MotionEvent.ACTION_UP -> {
                    addMonumentsViewBinding?.addMonumentLayoutNestedScrollLayout?.requestDisallowInterceptTouchEvent(true)
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    addMonumentsViewBinding?.addMonumentLayoutNestedScrollLayout?.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> false

            }
        }
    }

}