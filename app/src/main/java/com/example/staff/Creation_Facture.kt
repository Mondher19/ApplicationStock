package com.example.staff

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.appcompat.app.AlertDialog
import com.example.staff.model.Client
import com.example.staff.model.ProductAllocation
import com.example.staff.model.Produit
import com.example.staff.model.addFacture
import com.example.staff.service.ApiHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Creation_Facture.newInstance] factory method to
 * create an instance of this fragment.
 */
class Creation_Facture : Fragment() {

    private lateinit var clientSpinner: Spinner
    private lateinit var productListContainer: LinearLayout
    private lateinit var addProductBtn: ImageButton
    private lateinit var submitBtn: Button
    private var clientsList: List<Client> = listOf()
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
        val view = inflater.inflate(R.layout.fragment_creation__facture, container, false)

        initializeViews(view)
        populateClientSpinner()
        displayProducts()
        setButtonListeners()

        return view
    }



    private fun updateUIWithSelectedProduct(product: Produit, initialQuantity: Int) {
        val productRow = LayoutInflater.from(context).inflate(R.layout.product_row_layout, null)

        val productNameTV = productRow.findViewById<TextView>(R.id.productNameTV)
        val quantityET = productRow.findViewById<EditText>(R.id.quantityET)
        val addButton = productRow.findViewById<ImageButton>(R.id.addButton)
        val subtractButton = productRow.findViewById<ImageButton>(R.id.subtractButton)


        productNameTV.text = product.nom
        quantityET.setText(initialQuantity.toString())

        addButton.setOnClickListener {
            val currentQuantity = quantityET.text.toString().toInt()
            quantityET.setText((currentQuantity + 1).toString())

            // Update selectedProducts list
            val existingProduct = selectedProducts.find { it.first == product._id }
            if (existingProduct != null) {
                selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, existingProduct.second + 1)
            }
        }

        quantityET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Convert the editable content to an integer
                val newQuantity = s?.toString()?.toIntOrNull()

                // If the conversion is successful, update the selectedProducts list
                if (newQuantity != null) {
                    val existingProduct = selectedProducts.find { it.first == product._id }
                    if (existingProduct != null) {
                        selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, newQuantity)
                    }
                }
            }

            // The following functions are necessary but you won't need to modify them
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing here
            }
        })

        subtractButton.setOnClickListener {
            val currentQuantity = quantityET.text.toString().toInt()
            if (currentQuantity > 0) {
                quantityET.setText((currentQuantity - 1).toString())

                // Update selectedProducts list
                val existingProduct = selectedProducts.find { it.first == product._id }
                if (existingProduct != null) {
                    selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, existingProduct.second - 1)
                }
            }
        }

        productListContainer.addView(productRow)
    }

    private fun populateClientSpinner() {
        apiHelper.fetchclient { clients ->
            clientsList = clients
            val clientsNames = clients.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, clientsNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clientSpinner.adapter = adapter
        }
    }

    private fun initializeViews(view: View) {
        clientSpinner = view.findViewById(R.id.clientSpinner)
        productListContainer = view.findViewById(R.id.productListContainer)
        addProductBtn = view.findViewById(R.id.addProductBtn)
        submitBtn = view.findViewById(R.id.submitBtn)
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

    sealed class Result<out T> {
        data class Success<out T>(val value: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }


    private fun setButtonListeners() {
        addProductBtn.setOnClickListener {
            val selectedProductName = productSpinner.selectedItem as String
            val selectedProduct = productsList.find { it.nom == selectedProductName }

            if (selectedProduct != null) {
                val quantityText = quantityEditText.text.toString()
                val quantity = quantityText.toIntOrNull()

                if (quantityText.isEmpty() || quantityText.toIntOrNull() ?: 0 <= 0) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Erreur de saisie")
                        .setMessage("Veuillez entrer une quantité valide.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    return@setOnClickListener
                }

                val existingProduct = selectedProducts.find { it.first == selectedProduct._id }
                if (existingProduct != null) {
                    // If product exists, update its quantity in selectedProducts based on the UI value
                    for (i in 0 until productListContainer.childCount) {
                        val view = productListContainer.getChildAt(i)
                        val productNameTV = view.findViewById<TextView>(R.id.productNameTV)
                        val quantityET = view.findViewById<EditText>(R.id.quantityET)

                        if (productNameTV.text == selectedProduct.nom) {
                            val updatedQuantity = quantityET.text.toString().toInt()
                            selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, updatedQuantity)
                        }
                    }
                } else {
                    // If product doesn't exist, add it to selectedProducts and update the UI
                    selectedProducts.add(Pair(selectedProduct._id, quantity) as Pair<String, Int>)
                    if (quantity != null) {
                        updateUIWithSelectedProduct(selectedProduct, quantity)
                    }
                }
                quantityEditText.text.clear()
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
                    AlertDialog.Builder(requireContext())
                        .setTitle("Erreur de saisie")
                        .setMessage("Veuillez entrer une quantité valide à soustraire.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }



        submitBtn.setOnClickListener {
            val selectedVendeurName = clientSpinner.selectedItem as String
            val selectedClientName = clientsList.find { it.name == selectedVendeurName }
            val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
            val userfullnamefromshared = sharedPreferences.getString("name", null)
            if (selectedClientName.toString().isNotEmpty() && selectedProducts.isNotEmpty()) {
                val factureToSubmit = selectedClientName?.let { it1 ->
                    addFacture(
                        clientId = selectedClientName._id,
                        products = selectedProducts.map { ProductAllocation(it.first, it.second) },
                        credit = true, // Update this as needed, perhaps via a checkbox?
                        nomVendeur = userfullnamefromshared ?: "Unknown"  // Using the value from shared preferences
                    )
                }

                if (factureToSubmit != null) {
                    apiHelper.addFactureToApi(factureToSubmit) { result ->
                        when (result) {
                            is Result.Success<*> -> {
                                context?.let { context ->
                                    AlertDialog.Builder(context).apply {
                                        setTitle("Confirmation")
                                        setMessage("Facture créée avec succès!")
                                        setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                        show()
                                    }
                                }
                                productListContainer.removeAllViews()
                                selectedProducts.clear()
                            }


                            else -> {}
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Please select a client and add products!", Toast.LENGTH_LONG).show()
            }
        }
    }




    private fun updateExistingProductUI(product: Produit, quantity: Int) {
        for (i in 0 until productListContainer.childCount) {
            val view = productListContainer.getChildAt(i)
            val productNameTV = view.findViewById<TextView>(R.id.productNameTV)
            val quantityET = view.findViewById<EditText>(R.id.quantityET)

            if (productNameTV.text == product.nom) {
                val currentQuantity = quantityET.text.toString().toInt()
                quantityET.setText((currentQuantity + quantity).toString())
            }
        }
    }


    private fun removeProductFromList(product: Produit, quantity: Int) {
        for (i in 0 until productListContainer.childCount) {
            val view = productListContainer.getChildAt(i)
            val productNameTV = view.findViewById<TextView>(R.id.productNameTV)
            val quantityET = view.findViewById<EditText>(R.id.quantityET)

            if (productNameTV.text == product.nom) {
                val currentQuantity = quantityET.text.toString().toInt()

                if (currentQuantity > quantity) {
                    quantityET.setText((currentQuantity - quantity).toString())
                    val existingProduct = selectedProducts.find { it.first == product._id }
                    if (existingProduct != null) {
                        // Update existing quantity in the list
                        selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, existingProduct.second - quantity)
                    }
                } else {
                    // Remove the product from the list if its quantity becomes 0
                    productListContainer.removeView(view)
                    val productToRemove = selectedProducts.find { it.first == product._id }
                    selectedProducts.remove(productToRemove)
                }
            }
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