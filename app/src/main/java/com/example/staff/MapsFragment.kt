package com.example.staff

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private val firestore = FirebaseFirestore.getInstance()
    private val handler = Handler(Looper.getMainLooper())





    companion object {
        const val LOCATION_REQUEST_CODE = 101
        var isWorkEnqueued = false

    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // You can choose any value you prefer


    private lateinit var database: DatabaseReference

    private val SendLocation = object : Runnable {
        override fun run() {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(requireContext())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }



    class SendLocationWorker(appContext: Context, workerParams: WorkerParameters)
        : Worker(appContext, workerParams) {

        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        private val database = FirebaseDatabase.getInstance().reference
        private val sharedPreferences = appContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        override fun doWork(): Result {
            // Check permission
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Get the last known location and update Firebase
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val myLocation = LatLng(location.latitude, location.longitude)
                        saveLocationToFirebase(myLocation)
                    }
                }
            }

            return Result.success()
        }

        private fun saveLocationToFirebase(location: LatLng) {

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Log.e("SendLocationWorker", "User is not authenticated!")
                return
            }

            val locationData = mapOf(
                "latitude" to location.latitude,  // Using Double type
                "longitude" to location.longitude  // Using Double type
            )

            FirebaseFirestore.getInstance().collection("Users").document(userId).update(locationData)
                .addOnSuccessListener {
                    Log.d("SendLocationWorker", "Location updated successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("SendLocationWorker", "Failed to update location: ", e)
                }
        }
    }





    private fun saveLocationToFirebase(location: LatLng) {

        // Update SharedPreferences


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("SendLocationWorker", "User is not authenticated!")
            return
        }

        val locationData = mapOf(
            "latitude" to location.latitude,  // Using Double type
            "longitude" to location.longitude  // Using Double type
        )

        FirebaseFirestore.getInstance().collection("Users").document(userId).update(locationData)
            .addOnSuccessListener {
                Log.d("SendLocationWorker", "Location updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("SendLocationWorker", "Failed to update location: ", e)
            }
    }


    private fun showMyLocation() {
        googleMap.isMyLocationEnabled = false

        // Initialize the database reference (usually done in onCreate or onStart of the fragment/activity)
        database = FirebaseDatabase.getInstance().reference

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.camion)
            val scaledWidth = 100
            val scaledHeight = 100
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val myLocation = LatLng(location.latitude, location.longitude)

                    // Save the location to Firebase
                    saveLocationToFirebase(myLocation)
                    googleMap.addMarker(MarkerOptions().position(myLocation).title("My Location").icon(bitmapDescriptor))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.0f))
                    Log.d("MapsFragment", "My Location: $myLocation")
                }
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }








    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        val  mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener (this)
        setUpMap(mMap)

        handler.post(SendLocation)

        val icon1 = BitmapFactory.decodeResource(resources, R.drawable.camion)


        showMyLocation()
        setupSearchView()


    }






    private fun setUpMap(googleMap: GoogleMap) {

        // Check location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Permission is granted, enable location layer
            googleMap.isMyLocationEnabled = false


        } else {

            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)

        }

        // Get last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            // Check if location is not null
            if (location != null) {



            }
        }

    }

    private fun placeMarkeronMap(currentLatLong: LatLng,googleMap: GoogleMap){

        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        googleMap.addMarker(markerOptions)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


            val workRequest = PeriodicWorkRequestBuilder<SendLocationWorker>(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "SendLocationWork",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )


    }

    override fun onPause() {
        super.onPause()


    }


    private fun setupSearchView() {
        searchView = requireView().findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {
            false
        }
    }



    private fun addMarker(transportLocation: com.google.maps.model.LatLng) {
        val location = LatLng(transportLocation.lat, transportLocation.lng)
        googleMap.addMarker(MarkerOptions().position(location).title("Transport Location"))
        val icon1 = BitmapFactory.decodeResource(resources, R.drawable.camion)
        val scaledBitmap = Bitmap.createScaledBitmap(icon1, 100, 100, false)

        val markerOptions = MarkerOptions()
            .position(location)
            .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))// set custom icon
            .title("Current Location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
    }




    private fun search(query: String) {
        lifecycleScope.launch {
            val location = getFirstLocation(query)
            if (location != null) {
                addMarker(location)






            }
        }
    }




    private suspend fun getFirstLocation(query: String): LatLng? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext())
            val addresses = geocoder.getFromLocationName(query, 1)
            addresses!!.firstOrNull()?.run {
                if (hasLatitude() && hasLongitude()) {
                    LatLng(latitude, longitude)
                } else {
                    null
                }
            }
        }
    }

    private var firstMarkerLocation: LatLng? = null

    private fun addMarker(location: LatLng) {
        val icon1 = BitmapFactory.decodeResource(resources, R.drawable.camion)
        val scaledBitmap = Bitmap.createScaledBitmap(icon1, 100, 100, false)
        // set custom icon
        if (firstMarkerLocation == null) {
            firstMarkerLocation = location
            googleMap.addMarker(MarkerOptions().position(location).title("Search Result").icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)))
        } else {
            googleMap.addMarker(MarkerOptions().position(location).title("Search Result"))
            googleMap.addPolyline(PolylineOptions().add(firstMarkerLocation, location))
            firstMarkerLocation = null
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
    }

    override fun onMarkerClick(p0: Marker) = false





}