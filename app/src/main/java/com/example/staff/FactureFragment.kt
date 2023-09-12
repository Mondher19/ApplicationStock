package com.example.staff

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class FactureFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var searchView: SearchView
    private lateinit var adapter: FactureAdapter
    private lateinit var mList: List<Journal>

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
        val view = inflater.inflate(R.layout.fragment_facture, container, false)

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

        val recyclerview = view?.findViewById<RecyclerView>(R.id.recyclerviewfacture)
        searchView = view.findViewById(R.id.searchView)

        // Setup RecyclerView layout manager
        recyclerview?.layoutManager = LinearLayoutManager(requireContext())

        // Attach the adapter to the recyclerview
        recyclerview?.adapter = adapter

        // Fetch data from the API
        ApiHelper().fetchjournal { factures ->
            requireActivity().runOnUiThread {
                mList = factures
                adapter.setFilteredList(mList)
            }
        }

        // set the adapter with the recyclerview
        if (recyclerview != null) {
            recyclerview.adapter = adapter
        }

        // fetch data from the API
        // fetch data from the API
        ApiHelper().fetchjournal { factures ->
            requireActivity().runOnUiThread {
                mList = factures
                adapter.setFilteredList(mList)
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
            FactureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun filter(text: String?) {
        val filteredList: List<Journal> = if (text.isNullOrEmpty()) {
            mList // no filter, return the original list
        } else {
            mList.filter { it._id.contains(text, ignoreCase = true) } // filter the list based on text
        }
        adapter.setFilteredList(filteredList) // assign the filtered list to the adapter
    }

}