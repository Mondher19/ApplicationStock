package com.example.staff

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.adapters.Vendeur_venAdapter
import com.example.staff.model.Journal
import com.example.staff.model.Vendeur
import com.example.staff.service.ApiHelper
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [stationsadmin.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class Vendeur_vend_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchView: SearchView
    private lateinit var adapter: Vendeur_venAdapter
    private var mList: List<Vendeur> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vendeur_vend_, container, false)


        val backtbtn: ImageView = view.findViewById(R.id.imageView12)
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
        val userRoleFromShared = sharedPreferences.getString("role", null)

        val backBtn: ImageView = view.findViewById(R.id.imageView12)
        if (backBtn != null) {
            backBtn.setOnClickListener {
                val fragment: Fragment
                if (userRoleFromShared == "Vendeur") {
                    fragment = EspaceVendeurFragment()
                } else {
                    fragment = EspaceVendeurFragment()
                }

                val bundle = Bundle()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.switchfragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }








        // a changer



        // getting the recyclerview by its id
        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerviewvendeur)
        searchView = view.findViewById(R.id.searchView)
        // this creates a vertical layout Manager
        if (recyclerview != null) {
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
        }

        // initialize the adapter with an empty list
        adapter = Vendeur_venAdapter(emptyList())

        // set the adapter with the recyclerview
        if (recyclerview != null) {
            recyclerview.adapter = adapter
        }

        // fetch data from the API
        // fetch data from the API
        val userfullnamefromshared = sharedPreferences.getString("name", null)

// Fetch data from the API using the vendor name from shared preferences
        if (userfullnamefromshared != null) {
            ApiHelper().getVendeurByVendorName(userfullnamefromshared) { journals, error ->
                requireActivity().runOnUiThread {
                    if (error != null) {
                        Log.e("API_RESULT", "Error received: $error")
                    } else {
                        journals?.let {
                            Log.d("API_RESULT", "Data received: $journals")
                            mList = it // Update the mList with the fetched data
                            adapter.setFilteredList(mList) // Update the adapter with the fetched data
                        } ?: run {
                            Log.e("API_RESULT", "Received null journal list")
                        }
                    }
                }
            }
        }



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProduitFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun filter(text: String?) {
        val filteredList: List<Vendeur> = if (text.isNullOrEmpty()) {
            mList // No filter, return the original list
        } else {
            mList.filter { it.nom.contains(text, ignoreCase = true) } // Filter the list based on text
        }
        adapter.setFilteredList(filteredList) // Assign the filtered list to the adapter
    }

}