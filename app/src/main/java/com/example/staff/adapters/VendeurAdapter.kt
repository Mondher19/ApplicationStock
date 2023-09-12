package com.example.staff.adapters

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.ModifierVendeur
import com.example.staff.R
import com.example.staff.model.Vendeur
import com.example.staff.service.ApiHelper


 class VendeurAdapter(private var mList: List<Vendeur>) : RecyclerView.Adapter<VendeurAdapter.ViewHolder>() {

    var data: ArrayList<Vendeur> = ArrayList(mList) // update the data property with mList
    var listener: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vendeurrsadmin, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        val vendeur = mList[position]

        holder.nom.text = vendeur.nom

        // If Vendeur is validated
        if (vendeur.validation == true) {
            holder.validation.text = "Confirmé"
            holder.validation.setTextColor(Color.GREEN)
            holder.validerbtn.visibility = View.GONE
            holder.modifierbtn.visibility = View.GONE
            holder.productInfo.text = "Stock Vide"
        } else {
            holder.validation.text = "Non confirmé"
            holder.validation.setTextColor(Color.RED)
            holder.validerbtn.visibility = View.VISIBLE
            holder.modifierbtn.visibility = View.VISIBLE

            // Constructing the stock string to show only quantities without product IDs
            val stockString = StringBuilder()
            vendeur.stock.forEach { productInStock ->
                stockString.append("${productInStock.productId}: ${productInStock.quantite}\n")
            }
            holder.productInfo.text = stockString.toString()
        }

        fun refreshData() {
            ApiHelper().fetchvendeur { updatedList ->
                mList = updatedList
                data = ArrayList(updatedList)
                notifyDataSetChanged()
            }
        }



        holder.validerbtn.setOnClickListener {
            // Call your validation logic here
            Toast.makeText(holder.itemView.context, "Vendeur Validated", Toast.LENGTH_SHORT).show()

            // You can also update the UI after validation, for instance:
            holder.validation.text = "Confirmé"
            holder.validation.setTextColor(Color.GREEN)
            holder.validerbtn.visibility = View.GONE
            holder.modifierbtn.visibility = View.GONE
            holder.productInfo.text = "Stock Vide"

            // Call the validateVendeur function
            ApiHelper().validateVendeur(ItemsViewModel._id, ItemsViewModel.stock) {
                // Once validation is done, refresh the data
                refreshData()
            }
        }



        holder.modifierbtn.setOnClickListener {
            val fragment = ModifierVendeur()
            val bundle = Bundle()


            val vendeurId = ItemsViewModel._id
            bundle.putString("id_vendeur", vendeurId)
            bundle.putString("PRODUCT_Nom", ItemsViewModel.nom)
            bundle.putString("PRODUCT_Qantite", ItemsViewModel.stock.toString())

            fragment.arguments = bundle

            // Log the product ID for debugging purposes
            Log.d("ModifierClick", "Modifying product with ID: $vendeurId")

            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.switchfragment, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }




        // set click listener to view holder
        holder.itemView.setOnClickListener {
            val fragment = ModifierVendeur()
            val bundle = Bundle()


            val vendeurId = ItemsViewModel._id
            bundle.putString("id_vendeur", vendeurId)
            bundle.putString("PRODUCT_Nom", ItemsViewModel.nom)
            bundle.putString("PRODUCT_Qantite", ItemsViewModel.stock.toString())

            fragment.arguments = bundle

            // Log the product ID for debugging purposes
            Log.d("ModifierClick", "Modifying product with ID: $vendeurId")

            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.switchfragment, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

    }

     


    // add this for Filter (Search)
    fun setFilteredList(mList: List<Vendeur>) {
        this.mList = mList
        data = ArrayList(mList)
        notifyDataSetChanged()
    }

    // add this for Clearing the Data (Optional)
    fun clearData() {
        mList = emptyList()
        data = ArrayList()
        notifyDataSetChanged()
    }



    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nom: TextView = itemView.findViewById(R.id.nomvendeur)
        val productInfo: TextView = itemView.findViewById(R.id.stockvendeur)
        val validation: TextView = itemView.findViewById(R.id.Validationid)
        val modifierbtn: ImageButton = itemView.findViewById(R.id.modifierstockvendeur)
        val validerbtn: ImageButton = itemView.findViewById(R.id.validerstockvendeur)

    }
}