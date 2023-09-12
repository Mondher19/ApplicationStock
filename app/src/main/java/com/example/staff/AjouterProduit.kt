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




        //   val role: EditText = view.findViewById(R.id.role)








        addbtn.setOnClickListener {




            val newproduit = Produitadd(
                nom = nomproduit.text.toString(),
                prix = prixProduit.text.toString(),
                description = description.text.toString(),
                stock = quantiteproduit.text.toString(), //

            )

            ApiHelper().addproduitToApi(newproduit) {
                val message = "Produit Ajouter !!"
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
            AjouterProduit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}