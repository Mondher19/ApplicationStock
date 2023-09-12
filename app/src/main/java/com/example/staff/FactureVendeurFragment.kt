package com.example.staff

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.adapters.FactureAdapter
import com.example.staff.model.Journal
import com.example.staff.service.ApiHelper


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
class FactureVendeurFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchView: SearchView
    private lateinit var adapter: FactureAdapter
    private lateinit var dateSpinner: Spinner  // <-- Add this line

    private var mList: MutableList<Journal> = mutableListOf() // Initialize mList as mutable

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
        val view = inflater.inflate(R.layout.fragment_facture_vendeur, container, false)

        // Initialize the adapter with an empty list
        adapter = FactureAdapter(emptyList())

        // Log for debugging
        Log.d("FactureFragment", "Adapter initialized")

        // Set download listener
        adapter.downloadListener = { uri ->
            Log.d("FactureFragment", "Download listener invoked") // Add this line for debugging
            val request = DownloadManager.Request(uri)
                .setTitle("Your File Title")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "newFileName.pdf")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            downloadManager?.enqueue(request)
        }

        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerviewfacturev1)
        searchView = view.findViewById(R.id.searchView)

        // Setup RecyclerView layout manager
        recyclerview?.layoutManager = LinearLayoutManager(requireContext())

        val dateOptions = arrayOf("Today","This Week", "This Month")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateOptions)
        dateSpinner = view.findViewById(R.id.dateSpinner)

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Do your filtering logic based on the selected item
                filterByDate(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing selected
            }
        }



// set the adapter with the recyclerview
        if (recyclerview != null) {
            recyclerview.adapter = adapter
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
        val userfullnamefromshared = sharedPreferences.getString("name", null)

// Fetch data from the API using the vendor name from shared preferences
        if (userfullnamefromshared != null) {
            ApiHelper().getJournalByVendorName(userfullnamefromshared) { journals, error ->

                requireActivity().runOnUiThread {
                    if (error != null) {
                        Log.e("API_RESULT", "Error received: $error")
                    } else {
                        journals?.let {
                            Log.d("API_RESULT", "Data received: $journals")
                            // Assume mList is now of type MutableList<Journal> to keep things simple
                            mList.clear()
                            mList.addAll(journals)  // Use addAll to add the list of Journals
                            adapter.setFilteredList(mList)
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
            FactureVendeurFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun filterByDate(selectedItem: String) {
        val now = Calendar.getInstance()
        val filteredList: List<Journal> = when (selectedItem) {
            "Aujourdhui" -> {
                mList.filter {
                    val journalDate = Calendar.getInstance()
                    journalDate.time = it.date
                    journalDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            journalDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                }
            }
            "Cette Semaine" -> {
                mList.filter {
                    val journalDate = Calendar.getInstance()
                    journalDate.time = it.date
                    journalDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            journalDate.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
                }
            }
            "Ce Mois" -> {
                mList.filter {
                    val journalDate = Calendar.getInstance()
                    journalDate.time = it.date
                    journalDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            journalDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                }
            }
            else -> mList
        }
        adapter.setFilteredList(filteredList)
    }

    private fun filter(text: String?) {
        val filteredList: List<Journal> = if (text.isNullOrEmpty()) {
            mList // no filter, return the original list
        } else {
            mList.filter { it.vendeurName.contains(text, ignoreCase = true) } // filter the list based on text
        }
        adapter.setFilteredList(filteredList) // assign the filtered list to the adapter
    }

}