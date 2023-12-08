package com.example.staff

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.staff.model.Produitadd
import com.example.staff.service.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AjouterProduit : Fragment() {
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
        var view = inflater.inflate(R.layout.fragment_ajouter_produit, container, false)


        val addbtn: Button = view.findViewById(R.id.Ajouterproduitbtn)
        val nomproduit: EditText = view.findViewById(R.id.Nomproduitid)
        val prixProduit: EditText = view.findViewById(R.id.prixproduitid)
        val quantiteproduit: EditText = view.findViewById(R.id.Quantiteid)
        val description: EditText = view.findViewById(R.id.descriptionid)

        val backtbtn: ImageView = view.findViewById(R.id.imageView12)
        if (backtbtn != null) {
            backtbtn.setOnClickListener {
                val fragment = ProduitFragment()
                val bundle = Bundle()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.switchfragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }




        //   val role: EditText = view.findViewById(R.id.role)








        addbtn.setOnClickListener {

            // Validation for empty fields

            fun showSuccessDialog(message: String) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    // Navigate to ProduitFragment using FragmentManager
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.switchfragment, ProduitFragment())
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
            val nom = nomproduit.text.toString().trim()
            val prix = prixProduit.text.toString().trim()
            val descriptionText = description.text.toString()
            val stock = quantiteproduit.text.toString().trim()

            // Individual Validation checks
            if (nom.isBlank()) {
                showErrorDialog("Le nom du produit est obligatoire.")
                return@setOnClickListener
            }

            if (prix.isBlank()) {
                showErrorDialog("Le prix du produit est obligatoire.")
                return@setOnClickListener
            }

            try {
                prix.toDouble()
            } catch (e: NumberFormatException) {
                showErrorDialog("Le prix doit être un nombre valide.")
                return@setOnClickListener
            }

            if (description.text.toString().trim().isEmpty()) {
                description.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has" +
                        " been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley")
                return@setOnClickListener
            }




            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val newproduit = Produitadd(
                        nom = nomproduit.text.toString(),
                        prix = prixProduit.text.toString(),
                        description = description.text.toString(),
                        stock = quantiteproduit.text.toString()
                    )
                    val response = withContext(Dispatchers.IO) {
                        ApiHelper().addproduitToApi(newproduit)
                    }

                    if (response?.isSuccessful == true) {
                        showSuccessDialog("✅ Le produit a été ajouté avec succès.")
                    } else {
                        val errorMsg = response?.errorBody()?.string() ?: "Une erreur inconnue est survenue."
                        showErrorDialog("❌ Échec de l'ajout du produit ")
                    }
                } catch (e: Exception) {
                    showErrorDialog("❌ Échec de l'ajout du produit ")
                }
            }





        }



        return view
    }




    interface ApiCallback {
        fun onSuccess()
        fun onFailure(error: String)
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