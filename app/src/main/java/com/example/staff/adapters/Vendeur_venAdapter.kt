package com.example.staff.adapters

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.R
import com.example.staff.model.Vendeur
import com.example.staff.service.ApiHelper


class Vendeur_venAdapter(private var mList: List<Vendeur>) : RecyclerView.Adapter<Vendeur_venAdapter.ViewHolder>() {

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
            .inflate(R.layout.vendeurrs, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        val vendeur = mList[position]
        holder.productInfo.movementMethod = ScrollingMovementMethod()

        holder.nom.text = "Nom : " + vendeur.nom + "\n\n" +"Stock :"


        // If Vendeur is validated
        if (vendeur.validation == true) {
            holder.validation.text = "Stock Confirmé"
            holder.validation.setTextColor(Color.GREEN)

            holder.productInfo.text = "Stock Vide"
        } else {
            holder.validation.text = "Stock Non Confirmé"
            holder.validation.setTextColor(Color.RED)


            // Constructing the stock string to show only quantities without product IDs
            val stockString = SpannableStringBuilder()
            var start: Int
            var end: Int

            vendeur.stock.forEach { productInStock ->
                stockString.apply {
                    start = length


                    append("Nom : ${productInStock.productName}\n")
                    end = length
                    setSpan(AbsoluteSizeSpan(12, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append("Quantité : ${productInStock.quantite}\n\n")
                }
            }

            holder.productInfo.text = stockString
        }

         var vendeurName="user"









        // set click listener to view holder
        holder.itemView.setOnClickListener {
            showVendeurDetails(holder.itemView.context, vendeur)
        }

    }

    fun refreshData() {
        val vendeurName = "user" // This could be a parameter or some member variable
        ApiHelper().getVendeurByVendorName(vendeurName) { updatedList: List<Vendeur>?, _: String? ->
            updatedList?.let {
                mList = it
                data = ArrayList(it)
                notifyDataSetChanged()
            }
        }
    }

    private fun showVendeurDetails(context: Context, vendeur: Vendeur) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.vendeur_detail_modal, null)

        val nameTextView: TextView = dialogView.findViewById(R.id.nameTextView)
        val validationTextView: TextView = dialogView.findViewById(R.id.validationTextView)
        val stockLayout: LinearLayout  = dialogView.findViewById(R.id.stockLayout)

        nameTextView.text = "Nom vendeur : ${vendeur.nom}"
        validationTextView.text = if (vendeur.validation == true) "Stock Confirmé" else "Stock Non Confirmé"

        // Populate the stockLayout
        for (stockItem in vendeur.stock) {
            val stockTextView = TextView(context)
            stockTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            stockTextView.text = "Produit : ${stockItem.productName}, Quantité : ${stockItem.quantite}"
            stockLayout.addView(stockTextView)
        }

        builder.setView(dialogView)
            .setPositiveButton("Fermer") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
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

    }
}