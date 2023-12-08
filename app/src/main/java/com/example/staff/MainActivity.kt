package com.example.staff

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity(java: Class<MainActivity>) : AppCompatActivity() {


    private lateinit var bottomNavigationView: BottomNavigationView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1001
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        checkAndRequestPermissions()
        // Set the default night mode
        // Find the BottomNavigationView by its ID
        bottomNavigationView = findViewById(R.id.bottomNavigationView)






        Log.d("MainActivity", "onCreate called, checking permissions")



        // Set the listener for the bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {


                R.id.home -> {
                    changeFragment(EspaceVendeurFragment(), "ClientFragment")
                    return@setOnNavigationItemSelectedListener true
                }



                R.id.map -> {
                    changeFragment(MapsFragment(), "MapsFragment")
                    return@setOnNavigationItemSelectedListener true
                }



                R.id.settings -> {
                    changeFragment(SettingsFragment(), "SettingsFragment")
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }


    private fun showBackgroundLocationExplanation() {
        // For demonstration, I'm using a simple dialog. You can customize this or use other UI elements.
        AlertDialog.Builder(this)
            .setTitle("Background Location Permission")
            .setMessage("We need Your background location access")
            .setPositiveButton("Okay") { _, _ ->
                // After the user reads the explanation and clicks "Okay", request the actual permission
                requestBackgroundLocationPermission()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }




    private fun checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PermissionCheck", "Requesting FINE LOCATION permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d("PermissionCheck", "FINE LOCATION permission already granted")
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PermissionCheck", "Requesting BACKGROUND LOCATION permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d("PermissionCheck", "BACKGROUND LOCATION permission already granted")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("PermissionResult", "onRequestPermissionsResult called with requestCode: $requestCode")
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionResult", "FINE LOCATION permission granted")
                    showBackgroundLocationExplanation()
                } else {
                    Log.d("PermissionResult", "FINE LOCATION permission denied")
                }
            }
            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionResult", "BACKGROUND LOCATION permission granted")
                } else {
                    Log.d("PermissionResult", "BACKGROUND LOCATION permission denied")
                }
            }
        }
    }

    private fun changeFragment(fragment: Fragment, name: String) {
        if (name.isEmpty())
            supportFragmentManager.beginTransaction().replace(R.id.switchfragment, fragment).commit()
        else
            supportFragmentManager.beginTransaction().replace(R.id.switchfragment, fragment).addToBackStack(name).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Handle other menu items here
            else -> return super.onOptionsItemSelected(item)
        }
    }

}