package com.example.staff.adapters

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.ModifierProduit
import com.example.staff.R
import com.example.staff.model.Produit
import com.example.staff.service.ApiHelper


class ProduitAdapter(private var mList: List<Produit>) : RecyclerView.Adapter<ProduitAdapter.ViewHolder>() {

    var data: ArrayList<Produit> = ArrayList(mList) // update the data property with mList
    var listener: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.produitrsadmin, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.nom.text = ItemsViewModel.nom
        holder.prix.text = ItemsViewModel.prix + "  TND"
        holder.stock.text = ItemsViewModel.stock + " Unités"
        holder.description.text = ItemsViewModel.description
        holder.alerte.text = "Alerte < 30 Unités"


        holder.dotsbtn.setOnClickListener { view ->
            // Create and show a PopupMenu
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.product_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_modify -> {
                        // Your modify code here
                        val fragment = ModifierProduit()
                        val bundle = Bundle()

                        val productId = ItemsViewModel._id
                        bundle.putString("PRODUCT_ID", productId)
                        bundle.putString("PRODUCT_Nom", ItemsViewModel.nom)
                        bundle.putString("PRODUCT_Prix", ItemsViewModel.prix)
                        bundle.putString("PRODUCT_Qantite", ItemsViewModel.stock)
                        bundle.putString("PRODUCT_Description", ItemsViewModel.description)

                        fragment.arguments = bundle

                        val activity = holder.itemView.context as? AppCompatActivity
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.switchfragment, fragment)
                            ?.addToBackStack(null)
                            ?.commit()

                        true
                    }
                    R.id.action_delete -> {
                        // Show confirmation dialog
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Confirmation")
                            .setMessage("Êtes-vous sûr de vouloir supprimer ce produit?")
                            .setPositiveButton("Oui") { _, _ ->
                                // Delete the product if 'Yes' is clicked
                                ApiHelper().deleteproduitFromApi(ItemsViewModel._id) {
                                    Toast.makeText(holder.itemView.context, "Produit Supprimer", Toast.LENGTH_SHORT).show()
                                    mList = mList.filterIndexed { index, _ -> index != position }
                                    notifyDataSetChanged()
                                }
                            }
                            .setNegativeButton("Non", null) // Do nothing when 'No' is clicked
                            .show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }





        // set click listener to view holder
        holder.itemView.setOnClickListener {
            listener?.invoke(ItemsViewModel._id)
        }

    }

    // add this for Filter (Search)
    fun setFilteredList(mList: List<Produit>) {
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
        val nom: TextView = itemView.findViewById(R.id.nomproduit)
        val prix: TextView = itemView.findViewById(R.id.prixProduit)
        val stock: TextView = itemView.findViewById(R.id.quantiteproduit)
        val description: TextView = itemView.findViewById(R.id.Descriptionid)
        val alerte: TextView = itemView.findViewById(R.id.alerteproduit)

        val dotsbtn: ImageButton = itemView.findViewById(R.id.menuButton)


    }
}