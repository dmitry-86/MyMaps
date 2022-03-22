package com.netology.mymaps.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.utils.collection.addMarker
import com.netology.mymapapp.viewmodel.MapViewModel
import com.netology.mymaps.R

class CreateMapFragment : Fragment() {
    private lateinit var googleMap: GoogleMap
    private var markers: MutableList<Marker> = mutableListOf<Marker>()

    private val viewModel: MapViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSave) {
            if (markers.isEmpty()) {
                Toast.makeText(activity, "It must be at least one marker", Toast.LENGTH_LONG).show()
                return true
            }
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_createMapFragment_to_fragmentLocations)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_display_maps, container, false)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        lifecycle.coroutineScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap().apply {
                isTrafficEnabled = true
                isBuildingsEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }

            when {
                // 1. Проверяем есть ли уже права
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(it)
                    }
                }
                // 2. Должны показать обоснование необходимости прав
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // TODO: show rationale dialog
                }
                // 3. Запрашиваем права
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            val boundsBuilder = LatLngBounds.Builder()
            viewModel.data.observe(viewLifecycleOwner) {
                val markerManager = MarkerManager(googleMap)
                markerManager.newCollection().apply {
                    it.forEach() {
                        val target = LatLng(it.latitude, it.longitude)
                        boundsBuilder.include(target)
                        addMarker {
                            position(target)
                            title(it.title)
                        }
                    }
                }
            }

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    1000,
                    1000,
                    0
                )
            )

            googleMap.setOnInfoWindowClickListener { markerToDelete ->
                markers.remove(markerToDelete)
                markerToDelete.remove()
            }

            googleMap.setOnMapClickListener { latLng ->
                showAlertDialog(latLng)
            }
        }
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView =
            LayoutInflater.from(activity).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.create_marker)).setMessage(getString(R.string.hello))
            .setView(placeFormView)
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.ok), null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            if (title.trim().isEmpty()) {
                Toast.makeText(activity, getString(R.string.title_is_empty), Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(title))
            markers.add(marker!!)
            //сохранение маркеров
            viewModel.saveMarker(marker)
            dialog.dismiss()
        }
    }

}