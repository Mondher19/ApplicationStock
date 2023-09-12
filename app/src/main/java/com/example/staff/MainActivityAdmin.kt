package com.example.staff

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivityAdmin : AppCompatActivity() {


    private lateinit var bottomNavigationView: BottomNavigationView

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main_admin)
        // Set the default night mode
        // Find the BottomNavigationView by its ID
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set the listener for the bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {



                R.id.home -> {
                    changeFragment(ProduitFragment(), "ClientsFragment")
                    return@setOnNavigationItemSelectedListener true
                }


                R.id.map -> {
                    changeFragment(MapsFragmentAdmin(), "MapsAdminFragment")
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