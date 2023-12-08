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
import android.widget.Toast
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

        val backtbtn: ImageView = view.findViewById(R.id.imageView12)


    //    val btnSelectLocation: Button = view.findViewById(R.id.btnSelectLocation)

        val addbtn: Button = view.findViewById(R.id.Ajouterclientbtn)
        val FullName: EditText = view.findViewById(R.id.Nomcompletid)
        val EmailAdress: EditText = view.findViewById(R.id.Adressemailid)
        val Mobile: EditText = view.findViewById(R.id.Numtelid)
        val Adresse: EditText = view.findViewById(R.id.Adressesid)
        val Mat_fiscale: EditText = view.findViewById(R.id.mat_fiscaleid)

        val creditOptions = arrayOf("Oui", "Non")

        val adapter = ArrayAdapter(
            requireContext(), // Use `this` if you're in an Activity
            android.R.layout.simple_spinner_item,
            creditOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    //    creditSpinner.adapter = adapter



        //   val role: EditText = view.findViewById(R.id.role)

        val locationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
                selectedLongitude = data?.getDoubleExtra("longitude", 0.0)
            }
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
        val userRoleFromShared = sharedPreferences.getString("role", null)

        if (backtbtn != null) {
            backtbtn.setOnClickListener {
                val fragment: Fragment
                if (userRoleFromShared == "Magazinier") {
                    fragment = EspaceMagazinierFragment()
                } else if ( userRoleFromShared == "Superviseur")
                    fragment = EspaceSuperviseurFragment()
                else {
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






        addbtn.setOnClickListener {

            // Basic empty checks
            if (FullName.text.toString().isEmpty()) {
                Toast.makeText(context, "Le nom complet est obligatoire", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }








           // val location = Location(latitude = selectedLatitude!!, longitude = selectedLongitude!!)

            if (Mobile.text.toString().isEmpty() && EmailAdress.text.toString().isEmpty()) {
                Mobile.setText("0000000000")
                EmailAdress.setText("test@gmail.com")

            }


                // a changer location ici
                val newClient = Clientadd(
                    name = FullName.text.toString(),
                    numeroTel = Mobile.text.toString(),
                    email = EmailAdress.text.toString(),
                    credit = true,
                    qrCode = null,
                    location = null,
                     adresse=  Adresse.text.toString(),
                    Mat_fiscale= Mat_fiscale.text.toString(),
                )



            fun showSuccessDialog(message: String) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    // Navigate to ProduitFragment using FragmentManager
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.switchfragment, Client_Fragment_vendeur())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                builder.show()
            }

            fun showErrorDialog(message: String) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                builder.show()
            }

            ApiHelper().addClientToApi(
                newClient,
                successCallback = {
                    // On success, show success dialog and navigate to ProduitFragment
                    showSuccessDialog("✅ Le Client a été ajouté avec succès.")
                },
                errorCallback = { errorMsg ->
                    // On error, show error dialog
                    showErrorDialog("❌ Échec de l'ajout du client.")
                }
            )
        }



        return view
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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