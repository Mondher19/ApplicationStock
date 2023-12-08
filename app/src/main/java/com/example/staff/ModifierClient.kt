package com.example.staff

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.staff.model.Clientadd
import com.example.staff.model.Location
import com.example.staff.model.Produitadd
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
class ModifierClient : Fragment() {
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



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_modifier_client, container, false)


        val backtbtn: ImageView = view.findViewById(R.id.imageView12)

        val addbtn: Button = view.findViewById(R.id.Modifierclientbtn)
        val FullName: EditText = view.findViewById(R.id.Nomcompletid)
        val EmailAdress: EditText = view.findViewById(R.id.Adressemailid)
        val Mobile: EditText = view.findViewById(R.id.Numtelid)

        val Adresse: EditText = view.findViewById(R.id.Adressesid)
        val Mat_fiscale: EditText = view.findViewById(R.id.mat_fiscaleid)


        //   val role: EditText = view.findViewById(R.id.role)


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



        val nomcomplet = arguments?.getString("Client_Nom")
        val adresseemail = arguments?.getString("Client_email")
        val mobile = arguments?.getString("Client_telephone")
        val adresse = arguments?.getString("Client_Adresse")
        val mat_fiscale = arguments?.getString("Client_Mat_fiscale")


        val creditOptions = arrayOf("Oui", "Non")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            creditOptions
        )



        // Set values to EditText fields
        nomcomplet?.let {
            FullName.setText(it)
        }
        adresseemail?.let {
            EmailAdress.setText(it)
        }
        mobile?.let {
            Mobile.setText(it)
        }
        adresse?.let {
            Adresse.setText(it)
        }
        mat_fiscale?.let {
            Mat_fiscale.setText(it)
        }

        // Set Spinner value


        val locationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
                selectedLongitude = data?.getDoubleExtra("longitude", 0.0)
            }
        }





        addbtn.setOnClickListener {



            val location = if (selectedLatitude != null && selectedLongitude != null) {
                Location(latitude = selectedLatitude!!, longitude = selectedLongitude!!)
            } else {
                null
            }

            if (FullName.text.toString().isEmpty()) {
                Toast.makeText(context, "Le nom complet est obligatoire", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (Mobile.text.toString().isEmpty() && EmailAdress.text.toString().isEmpty()) {
                Mobile.setText("0000000000")
                EmailAdress.setText("test@gmail.com")

            }



            val modiferClient = Clientadd(
                name = FullName.text.toString(),
                numeroTel = Mobile.text.toString(),
                email = EmailAdress.text.toString(),
                credit = true,
                qrCode = null,
                location = location,
                adresse =  Adresse.text.toString(),
                Mat_fiscale= Mat_fiscale.text.toString()
            )



            val clientId = arguments?.getString("Client_ID")

            Log.d("UpdateProduct", "Button clicked. Attempting to fetch product ID.")

            // Assuming you have the product's ID (maybe passed to this fragment or fetched earlier)
            if (clientId != null) {
                Log.d("UpdateProduct", "Product ID fetched: $clientId")
                ApiHelper().updateClientToApi(clientId, modiferClient) {
                    val message = "Client Modifié !!"
                    Log.d("Update Client", "API call successful: $message")
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(message)
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            } else {
                Log.e("UpdateProduct", "Client ID is null!")
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
            AjouterProduit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}