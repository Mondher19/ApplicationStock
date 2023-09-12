package com.example.staff

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.staff.model.Produit
import com.example.staff.model.Vendeur
import com.example.staff.model.products
import com.example.staff.service.ApiHelper
import com.google.android.material.textfield.TextInputLayout

class ModifierVendeur : Fragment() {

    private lateinit var vendeurSpinner: Spinner
    private lateinit var productListContainer: LinearLayout
    private lateinit var addProductBtn: ImageButton
    private lateinit var submitBtn: Button
    private var vendeursList: List<Vendeur> = listOf()
    private var productsList: List<Produit> = listOf()

    private lateinit var productSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private val selectedProducts = mutableListOf<Pair<String, Int>>()  // List to hold product ID and quantity pairs
    private lateinit var subtractProductBtn: ImageButton



    private val apiHelper = ApiHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_modifier_vendeur, container, false)

        initializeViews(view)

        displayProducts()
        setButtonListeners()

        return view
    }

    private fun initializeViews(view: View) {
        productListContainer = view.findViewById(R.id.productListContainer)
        addProductBtn = view.findViewById(R.id.addProductBtn)
        submitBtn = view.findViewById(R.id.ModifiervendeurBtn)
        productSpinner = view.findViewById(R.id.productSpinner)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        subtractProductBtn = view.findViewById(R.id.subtractProductBtn)
    }



    private fun displayProducts() {
        apiHelper.fetchproduit { produits ->
            productsList = produits
            val productNames = produits.map { it.nom }
            val productAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productNames)
            productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            productSpinner.adapter = productAdapter
        }
    }


    private fun setButtonListeners() {
        addProductBtn.setOnClickListener {
            val selectedProductName = productSpinner.selectedItem as String
            val selectedProduct = productsList.find { it.nom == selectedProductName }

            if (selectedProduct != null) {
                val quantityText = quantityEditText.text.toString()
                val quantity = quantityText.toIntOrNull()
                if (quantity != null && quantity > 0) {
                    selectedProducts.add(Pair(selectedProduct._id, quantity))
                    updateUIWithSelectedProduct(selectedProduct, quantity)
                    quantityEditText.text.clear()
                } else {
                    (quantityEditText.parent.parent as TextInputLayout).error = "Please enter a valid quantity"
                }
            }
        }

        subtractProductBtn.setOnClickListener {
            val selectedProductName = productSpinner.selectedItem as String
            val selectedProduct = productsList.find { it.nom == selectedProductName }

            if (selectedProduct != null) {
                val quantityText = quantityEditText.text.toString()
                val quantity = quantityText.toIntOrNull()
                if (quantity != null && quantity > 0) {
                    removeProductFromList(selectedProduct, quantity)
                    quantityEditText.text.clear()
                } else {
                    (quantityEditText.parent.parent as TextInputLayout).error = "Please enter a valid quantity to subtract"
                }
            }
        }

        submitBtn.setOnClickListener {
            val idvendeur = arguments?.getString("id_vendeur")
            Log.d("MyFragment", "idvendeur: $idvendeur")

            val selectedVendeur = vendeursList.find { it._id == idvendeur }  // Assuming the property for vendeur's ID is "_id"



                val productAllocations = selectedProducts.map { products(it.first, it.second) }
                selectedProducts.clear()
                Toast.makeText(context, "Stock Allocated Successfully!", Toast.LENGTH_LONG).show()

                productListContainer.removeAllViews()

                if (idvendeur != null) {
                    apiHelper.Allocateproducts(idvendeur, productAllocations) {


                    }
                }

        }
    }


    private fun removeProductFromList(product: Produit, quantity: Int) {
        var foundProductView: TextView? = null
        for (i in 0 until productListContainer.childCount) {
            val view = productListContainer.getChildAt(i) as? TextView
            view?.let {
                if (it.text.contains(product.nom)) {
                    foundProductView = it
                }
            }
        }

        if (foundProductView != null) {
            val currentQuantity = Regex("(?<=Quantity: )\\d+").find(foundProductView!!.text)?.value?.toInt() ?: 0
            if (currentQuantity > quantity) {
                foundProductView!!.text = "${product.nom} (Quantity: ${currentQuantity - quantity})"
            } else {
                // Remove the product from the list if its quantity becomes 0
                productListContainer.removeView(foundProductView)
                val productToRemove = selectedProducts.find { it.first == product._id }
                selectedProducts.remove(productToRemove)
            }
        }
    }

    private fun updateUIWithSelectedProduct(product: Produit, quantity: Int) {
        // Check if product is already present in the list
        var foundProductView: TextView? = null
        for (i in 0 until productListContainer.childCount) {
            val view = productListContainer.getChildAt(i) as? TextView
            view?.let {
                if (it.text.contains(product.nom)) {  // Assuming 'nom' is the name of the product in the Produit class
                    foundProductView = it
                }
            }
        }

        if (foundProductView != null) {
            // Update quantity if product is already in the list
            val currentQuantity = Regex("(?<=Quantity: )\\d+").find(foundProductView!!.text)?.value?.toInt() ?: 0
            foundProductView!!.text = "${product.nom} (Quantity: ${currentQuantity + quantity})"
        } else {
            // Add a new entry if product is not already in the list
            val productTextView = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "${product.nom} (Quantity: $quantity)"
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
            productListContainer.addView(productTextView)
        }
    }
    companion object {
        fun newInstance(param1: String, param2: String) =
            Affectation_Stock_Vendeur().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}