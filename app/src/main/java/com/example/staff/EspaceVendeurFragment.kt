package com.example.staff

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EspaceVendeurFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EspaceVendeurFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_espace_vendeur, container, false)
        val ConsulterstockBtn: CardView = view.findViewById(R.id.Consulterstockbtn)
        val consulterFactureBtn: CardView = view.findViewById(R.id.Consulterfacturebtn)
        val consulterClientBtn: CardView = view.findViewById(R.id.ConsulterClientbtn)

        val Ajouterfacturebtn: CardView = view.findViewById(R.id.Ajouterfacturebtn)

        consulterFactureBtn.setOnClickListener { navigateToFragment(FactureVendeurFragment()) }
        Ajouterfacturebtn.setOnClickListener { navigateToFragment(Creation_Facture()) }
        ConsulterstockBtn.setOnClickListener { navigateToFragment(Vendeur_vend_Fragment()) }
        consulterClientBtn.setOnClickListener { navigateToFragment(Client_Fragment_vendeur()) }


        return view
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.Consulterfacturebtn -> navigateToFragment(FactureVendeurFragment())
            R.id.Ajouterfacturebtn -> navigateToFragment(Creation_Facture())
            R.id.Consulterstockbtn -> navigateToFragment(Vendeur_vend_Fragment())
            R.id.ConsulterVendeurbtn -> navigateToFragment(VendeurFragment())

        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.switchfragment, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EspaceVendeurFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EspaceVendeurFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}