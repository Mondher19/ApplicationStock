package com.example.nomadis.Worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val database = FirebaseDatabase.getInstance().reference

    override fun doWork(): Result {
        try {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, // <-- Corrected this
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    applicationContext, // <-- Corrected this
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions are not granted
                // For now, we will return failure. In the future, you may decide to handle this differently.
                return Result.failure()
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val myLocation = LatLng(location.latitude, location.longitude)

                    // Save the location to Firebase
                    saveLocationToFirebase(myLocation)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun saveLocationToFirebase(location: LatLng) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("MapsFragment", "User is not authenticated!")
            return
        }

        // Create a new location object to store in Firebase
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude
        )

        // Update the location under the user's ID in the 'users' collection
        firestore.collection("Users").document(userId).update(locationData)
            .addOnSuccessListener {
                Log.d("MapsFragment", "Location updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("MapsFragment", "Failed to update location: ", e)
            }
    }
}
