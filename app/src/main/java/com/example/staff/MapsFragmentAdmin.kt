package com.example.staff

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoFire

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.withContext

class MapsFragmentAdmin : Fragment(), GoogleMap.OnMarkerClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var searchView: SearchView
    private val clickedMarkers = HashSet<Marker>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private val firestore = FirebaseFirestore.getInstance()
    private val handler = Handler(Looper.getMainLooper())
    private val markersList: MutableList<Marker> = mutableListOf()

    val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var mMap: GoogleMap

    companion object {
        const val LOCATION_REQUEST_CODE = 101
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // You can choose any value you prefer


    private lateinit var database: DatabaseReference

    private val retrieveLocationsRunnable = object : Runnable {
        override fun run() {
            retrieveAndPlotUserLocations()
            // handler.postDelayed(this, 900000) // 15 minutes in milliseconds
            handler.postDelayed(this, 20000)
        }
    }

    private fun startCallIntent(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        if (context?.let { intent.resolveActivity(it.packageManager) } != null) {
            startActivity(intent)
        } else {
        }
    }


    private fun retrieveAndPlotUserLocations() {
        val db = FirebaseFirestore.getInstance()

        // Clear the previous markers from the map
        for (marker in markersList) {
            marker.remove()
        }
        markersList.clear()

        Log.d("MapsActivity", "Attempting to retrieve user locations...")

        db.collection("Users").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val latitude = document.getDouble("latitude")
                val longitude = document.getDouble("longitude")
                val number = document.getString("number")
                val name = document.getString("name")
                val role = document.getBoolean("role")

                if (role == true) {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.camion)
                    val scaledWidth = 100
                    val scaledHeight = 100
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
                    val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                    if (latitude != null && longitude != null) {
                        val userLocation = LatLng(latitude, longitude)
                        Log.d("MapsActivity", "Plotting location for user at latitude: $latitude, longitude: $longitude.")
                        val marker = googleMap?.addMarker(MarkerOptions().position(userLocation).title("$name").icon(bitmapDescriptor))
                        marker?.let {
                            it.tag = number
                            markersList.add(it)
                        }
                    } else {
                        Log.e("MapsActivity", "Latitude or Longitude data missing for user: ${document.id}")
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MapsActivity", "Error fetching data from Firestore: ", exception)
        }
    }




    private fun addMarkerToMap(location: LatLng) {
        // Assuming you're using Google Maps API, you'll typically have a GoogleMap object initialized.
        // You'd add a marker to this map like this:
        // mMap.addMarker(MarkerOptions().position(location).title("User Location"))
    }



    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        val mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap(mMap)

        val icon1 = BitmapFactory.decodeResource(resources, R.drawable.camion)


        // Specify the exact location coordinates (latitude and longitude)
        val specificLocation = LatLng(36.7958168, 10.0619071) // Replace with your desired coordinates

        // Specify the zoom level (e.g., 10.0f for a 10x zoom)
        val zoomLevel = 10.0f

        // Move the camera to the specific location with the desired zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(specificLocation, zoomLevel))

        // Start the periodic task
        handler.post(retrieveLocationsRunnable)

        // Set OnMarkerClickListener
        googleMap?.setOnInfoWindowClickListener { marker ->
            val phoneNumber = marker.tag as? String
            if (!phoneNumber.isNullOrEmpty()) {
                startCallIntent(phoneNumber)
            }
        }
        setupSearchView()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(retrieveLocationsRunnable)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
        val transportid = sharedPreferences.getString("transportid", null)

        val stationtype = sharedPreferences.getString("stationtype", null)
        // Call fetchLocationsFromApi() when the fragment is created

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