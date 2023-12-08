package com.example.staff.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
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
        holder.productInfo.movementMethod = ScrollingMovementMethod()

        holder.nom.text = "Nom : " + vendeur.nom + "\n\n" +"Stock :"


        val progressionAsInt = vendeur.progression.toInt() // This will cast the value to an integer



        // If Vendeur is validated
        if (vendeur.validation == true) {
            holder.validation.text = "Stock Confirmé"
            holder.validation.setTextColor(Color.GREEN)
            holder.validerbtn.visibility = View.GONE
            holder.productInfo.text = "Stock Vide"
            holder.progressBar.visibility = View.GONE
            holder.percentageTextView.visibility = View.GONE


        } else {
            holder.validation.text = "Stock Non Confirmé"
            holder.validation.setTextColor(Color.RED)
            holder.validerbtn.visibility = View.VISIBLE

            holder.progressBar.progress = progressionAsInt
            holder.percentageTextView.text = (progressionAsInt as Int).toString() +"%"
            when {
                progressionAsInt <= 30 -> {
                    holder.progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
                    holder.percentageTextView.setTextColor(Color.RED)
                }
                progressionAsInt in 31..70 -> {
                    holder.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFA500"))  // Orange color
                    holder.percentageTextView.setTextColor(Color.parseColor("#FFA500"))  // Orange color
                }
                progressionAsInt >= 71 -> {
                    holder.progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
                    holder.percentageTextView.setTextColor(Color.GREEN)
                }
            }
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

        fun refreshData() {
            ApiHelper().fetchvendeur { updatedList ->
                mList = updatedList
                data = ArrayList(updatedList)
                notifyDataSetChanged()
            }
        }





        holder.validerbtn.setOnClickListener {
            // Create an AlertDialog to confirm the validation
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Validation de vendeur")
            builder.setMessage("Êtes-vous sûr de vouloir valider le stock de ce vendeur?")

            // Set up the buttons
            builder.setPositiveButton("Oui") { dialog, which ->
                // User clicked Yes button

                // Call your validation logic here
                Toast.makeText(holder.itemView.context, "Vendeur Validé", Toast.LENGTH_SHORT).show()

                // You can also update the UI after validation, for instance:
                holder.validation.text = "Stock Confirmé"
                holder.validation.setTextColor(Color.GREEN)
                holder.validerbtn.visibility = View.GONE
                holder.productInfo.text = "Stock Vide"
                holder.progressBar.visibility = View.GONE
                holder.percentageTextView.visibility = View.GONE

                // Call the validateVendeur function
                ApiHelper().validateVendeur(ItemsViewModel._id, ItemsViewModel.stock) {
                    // Once validation is done, refresh the data
                    refreshData()

                }

            }

            builder.setNegativeButton("Non") { dialog, which ->
                // User cancelled the dialog
                dialog.cancel()
            }

            // Show the AlertDialog
            builder.show()
        }







        // set click listener to view holder
        holder.itemView.setOnClickListener {
            refreshData()
            showVendeurDetails(holder.itemView.context, vendeur)

        }

    }

     @SuppressLint("MissingInflatedId")
     private fun showVendeurDetails(context: Context, vendeur: Vendeur) {
         val builder = AlertDialog.Builder(context)
         val inflater = LayoutInflater.from(context)
         val dialogView = inflater.inflate(R.layout.vendeur_detail_modal, null)

         val nameTextView: TextView = dialogView.findViewById(R.id.nameTextView)
         val validationTextView: TextView = dialogView.findViewById(R.id.validationTextView)
         val stockLayout: LinearLayout = dialogView.findViewById(R.id.stockLayout)
         val addCreditButton: ImageButton = dialogView.findViewById(R.id.addCreditButton)
         val resetCreditButton: ImageButton = dialogView.findViewById(R.id.validerstockvendeur2)
         val creditTextView: TextView = dialogView.findViewById(R.id.credit)
         if (vendeur.credit.toString() == "0") {
             resetCreditButton.visibility = View.GONE
         }
         if (vendeur.credit.toString() == "0.0") {
             resetCreditButton.visibility = View.GONE
         } else {
             resetCreditButton.visibility = View.VISIBLE
         }
         nameTextView.text = "Nom vendeur : ${vendeur.nom}"
         creditTextView.text = "Total Credit vendeur : ${vendeur.credit.toFloat()}" + " Dt"


         validationTextView.text = if (vendeur.validation == true) "Stock Confirmé" else "Stock Non Confirmé"

         // Populate the stockLayout
         for (stockItem in vendeur.stock) {
             val stockTextView = TextView(context)
             stockTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
             stockTextView.text = "Produit : ${stockItem.productName}, Quantité : ${stockItem.quantite}"
             stockLayout.addView(stockTextView)
         }


         fun refreshData() {
             ApiHelper().fetchvendeur { updatedList ->
                 mList = updatedList
                 data = ArrayList(updatedList)
                 notifyDataSetChanged()
             }
         }




         resetCreditButton.setOnClickListener {
             // Use context to create AlertDialog
             val builder = AlertDialog.Builder(context)
             builder.setTitle("Réinitialiser le crédit")
             builder.setMessage("Êtes-vous sûr de vouloir réinitialiser le crédit du vendeur?")

             builder.setPositiveButton("Oui") { dialog, which ->
                 // Assuming vendeur is the current Vendeur object you're working with
                 val vendeurId = vendeur._id // replace with actual ID
                 resetCreditButton.requestLayout() // Invalidate layout

                 // Call the resetVendeurCredit method with the vendeurId
                 ApiHelper().resetVendeurCredit(vendeurId) {
                     creditTextView.text = "Total Credit vendeur : 0 Dt"
                     if (vendeur.credit.toString() == "0.0") {
                         resetCreditButton.visibility = View.GONE
                     } else {
                         resetCreditButton.visibility = View.VISIBLE
                     }
                     resetCreditButton.visibility = View.GONE
                     refreshData()   // Handle what happens after the reset here, like updating UI elements or notifying the user
                 }
             }

             builder.setNegativeButton("Non") { dialog, which ->
                 // Handle the cancel action here
             }

             builder.create().show()

         }

         addCreditButton.setOnClickListener {
             // Inflate the custom layout using context
             val inflater = LayoutInflater.from(context)
             val dialogLayout: View = inflater.inflate(R.layout.dialog_layout, null)
             val editText: EditText = dialogLayout.findViewById(R.id.dialog_edittext)

             // Use context to create AlertDialog
             val builder = AlertDialog.Builder(context)
             builder.setTitle("Ajouter un credit au vendeur")
             builder.setView(dialogLayout)
             builder.setPositiveButton("Valider") { dialog, which ->
                 val enteredCredit = editText.text.toString().toFloat()

                 // Check if enteredCredit is a valid number
                 if (enteredCredit != null) {
                     // Assuming vendeur is the current Vendeur object you're working with
                     val vendeurId = vendeur._id // replace with actual ID

                     // Call the updateVendeurCredit method with the vendeurId and the enteredCredit
                     ApiHelper().updateVendeurCredit(vendeurId, enteredCredit) {
                         // Retrieve the updated credit value here
                         ApiHelper().fetchvendeur() { updatedVendeur ->
                             // Update the UI with the new credit value
                             val updatedVendeur = updatedVendeur.find { it._id == vendeur._id }

                             updatedVendeur?.let {
                                 // Update the creditTextView with the updated credit value
                             //    creditTextView.text = "Total Credit vendeur : ${it.credit}" + " Dt"


                             if (it.credit.toString() == "0.0") {
                                 resetCreditButton.visibility = View.GONE
                             } else {
                                 resetCreditButton.visibility = View.VISIBLE
                             } }
                             Toast.makeText(context, "Crédit ajouté avec succès", Toast.LENGTH_SHORT).show()
                             refreshData()
                         }
                     }
                 } else {
                     // Handle invalid input
                     Toast.makeText(context, "Valeur de crédit invalide", Toast.LENGTH_SHORT).show()
                 }
             }
             builder.setNegativeButton("Annuler") { dialog, which ->
                 // Handle the cancel action here
             }
             builder.create().show()

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
        val credit: TextView = itemView.findViewById(R.id.nomvendeur)

        val productInfo: TextView = itemView.findViewById(R.id.stockvendeur)
        val validation: TextView = itemView.findViewById(R.id.Validationid)
        val validerbtn: ImageButton = itemView.findViewById(R.id.validerstockvendeur)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val percentageTextView: TextView = itemView.findViewById(R.id.percentageTextView)
    }
}