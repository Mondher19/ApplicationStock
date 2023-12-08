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
 * Use the [EspaceSuperviseurFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EspaceSuperviseurFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_espace_superviseur, container, false)

        val consulterFactureBtn: CardView = view.findViewById(R.id.Consulterfacturebtn)
        val consulterClientBtn: CardView = view.findViewById(R.id.ConsulterClientbtn)
        val consulterStockBtn: CardView = view.findViewById(R.id.consulterstockbtn)
        val consulterVendeurBtn: CardView = view.findViewById(R.id.consultervendeurbtn)

        consulterFactureBtn.setOnClickListener { navigateToFragment(FactureFragment()) }
        consulterClientBtn.setOnClickListener { navigateToFragment(ClientFragment()) }
        consulterStockBtn.setOnClickListener { navigateToFragment(ProduitFragment()) }
        consulterVendeurBtn.setOnClickListener { navigateToFragment(VendeurFragment()) }

        return view
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.Consulterfacturebtn -> navigateToFragment(FactureFragment())
            R.id.ConsulterClientbtn -> navigateToFragment(ClientFragment())
            R.id.ConsulterStockbtn -> navigateToFragment(ProduitFragment())
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
         * @return A new instance of fragment EspaceSuperviseurFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EspaceSuperviseurFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}