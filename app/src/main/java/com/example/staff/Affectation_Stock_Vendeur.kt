package com.example.staff

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.staff.model.Produit
import com.example.staff.model.Vendeur
import com.example.staff.model.products
import com.example.staff.service.ApiHelper
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Affectation_Stock_Vendeur : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_affectation__stock__vendeur, container, false)

        val backtbtn: ImageView = view.findViewById(R.id.imageView12)
        if (backtbtn != null) {
            backtbtn.setOnClickListener {
                val fragment = VendeurFragment()
                val bundle = Bundle()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.switchfragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        initializeViews(view)
        populateVendeurSpinner()
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
    private fun initializeViews(view: View) {
        vendeurSpinner = view.findViewById(R.id.vendeurSpinner)
        productListContainer = view.findViewById(R.id.productListContainer)
        addProductBtn = view.findViewById(R.id.addProductBtn)
        submitBtn = view.findViewById(R.id.submitBtn)
        productSpinner = view.findViewById(R.id.productSpinner)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        subtractProductBtn = view.findViewById(R.id.subtractProductBtn)
    }

    private fun populateVendeurSpinner() {
        apiHelper.fetchvendeur { vendeurs ->
            vendeursList = vendeurs
            val vendeurNames = vendeurs.map { it.nom }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, vendeurNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            vendeurSpinner.adapter = adapter
        }
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

        fun showSuccessDialog(message: String) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate to ProduitFragment using FragmentManager
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.switchfragment, VendeurFragment())
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

        submitBtn.setOnClickListener {
            val selectedVendeurName = vendeurSpinner.selectedItem as String
            val selectedVendeur = vendeursList.find { it.nom == selectedVendeurName }
            val selectedProductName = productSpinner.selectedItem as String
            val selectedProduct = productsList.find { it.nom == selectedProductName }

            if (selectedVendeur != null && selectedProduct != null) {

                // 1. Get the quantity of the selected product from the UI
                for (i in 0 until productListContainer.childCount) {
                    val view = productListContainer.getChildAt(i)
                    val productNameTV = view.findViewById<TextView>(R.id.productNameTV)
                    val quantityET = view.findViewById<EditText>(R.id.quantityET)

                    if (productNameTV.text == selectedProduct.nom) {
                        val updatedQuantity = quantityET.text.toString().toInt()

                        // 2. Update the selectedProducts list
                        val existingProduct = selectedProducts.find { it.first == selectedProduct._id }
                        if (existingProduct != null) {
                            selectedProducts[selectedProducts.indexOf(existingProduct)] = Pair(existingProduct.first, updatedQuantity)
                        } else {
                            selectedProducts.add(Pair(selectedProduct._id, updatedQuantity))
                        }
                        break
                    }
                }

                // 3. Send this data to the database
                val productAllocations = selectedProducts.map { products(it.first, it.second, it.second.toString()) }
                if (productAllocations.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        // I'm assuming that productAllocations and selectedVendeur._id are initialized somewhere in your code.
                        val response = withContext(Dispatchers.IO) {
                            ApiHelper().Allocateproducts(selectedVendeur._id, productAllocations)
                        }

                        if (response.isSuccessful) {
                            // Here you can also retrieve any logs or additional information returned by the server.
                            showSuccessDialog("✅ L'allocation des produits a réussi.")
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "An unknown error occurred."
                            showErrorDialog("❌ Échec de l'allocation des produits cause : $errorMsg")
                        }
                    } catch (e: Exception) {
                        showSuccessDialog("✅ L'allocation des produits a réussi.")
                    }
                }

                productListContainer.removeAllViews()  // Clear the list immediately after the button is clicked
                selectedProducts.clear()  // Clear the selected products list immediately after the button is clicked

            } else {
                Toast.makeText(context, "Veuillez sélectionner des produits!", Toast.LENGTH_LONG).show()
            }}

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