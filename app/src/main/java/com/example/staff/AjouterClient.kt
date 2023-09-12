package com.example.staff

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import com.example.staff.model.Clientadd
import com.example.staff.model.Location
import com.example.staff.service.ApiHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AjouterClient : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

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
        var view = inflater.inflate(R.layout.fragment_ajouter_client, container, false)

        val backtbtn: ImageView = view.findViewById(R.id.backtn9)
        val btnSelectLocation: Button = view.findViewById(R.id.btnSelectLocation)

        val addbtn: Button = view.findViewById(R.id.Ajouterclientbtn)
        val FullName: EditText = view.findViewById(R.id.Nomcompletid)
        val EmailAdress: EditText = view.findViewById(R.id.Adressemailid)
        val Mobile: EditText = view.findViewById(R.id.Numtelid)
        val creditSpinner: Spinner = view.findViewById(R.id.CreditSpinner)

        val creditOptions = arrayOf("Oui", "Non")

        val adapter = ArrayAdapter(
            requireContext(), // Use `this` if you're in an Activity
            android.R.layout.simple_spinner_item,
            creditOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        creditSpinner.adapter = adapter



        //   val role: EditText = view.findViewById(R.id.role)

        val locationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
                selectedLongitude = data?.getDoubleExtra("longitude", 0.0)
            }
        }

        if (backtbtn != null) {
            backtbtn.setOnClickListener {
                val fragment = ClientFragment()
                val bundle = Bundle()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.switchfragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        fun getCreditBoolean(): Boolean {
            val selectedCreditOption = creditSpinner.selectedItem.toString()
            Log.d("DEBUG_TAG", "Selected Credit Option: $selectedCreditOption")

            return when (selectedCreditOption) {
                "Oui" -> true
                else -> false  // This will handle the "Non" case and any unexpected values
            }
        }

        btnSelectLocation.setOnClickListener {
            val intent = Intent(requireContext(), SelectLocationActivity::class.java)
            locationResultLauncher.launch(intent)
        }



        addbtn.setOnClickListener {


            val creditBoolean = getCreditBoolean()
            Log.d("DEBUG_TAG", "Credit Boolean Value: $creditBoolean") // This will log the boolean representation

            val location = if (selectedLatitude != null && selectedLongitude != null) {
                Location(latitude = selectedLatitude!!, longitude = selectedLongitude!!)
            } else {
                null
            }



            val newClient = Clientadd(
                name = FullName.text.toString(),
                numeroTel = Mobile.text.toString(),
                email = EmailAdress.text.toString(),
                credit = creditBoolean, // Default value or fetch from a UI element
                qrCode = null,  // Add a QR code if available or leave as null
                location = location
            )

            ApiHelper().addClientToApi(newClient) {
                val message = "Client Ajouter !!"
                val builder = AlertDialog.Builder(context)
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }



        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AjouterClient().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}