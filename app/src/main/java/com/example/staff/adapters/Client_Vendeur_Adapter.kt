package com.example.staff.adapters

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
import com.example.staff.ModifierClient
import com.example.staff.ModifierProduit
import com.example.staff.R
import com.example.staff.model.Client
import com.example.staff.service.ApiHelper


class Client_Vendeur_Adapter (private var mList: List<Client>) : RecyclerView.Adapter<Client_Vendeur_Adapter.ViewHolder>(){
    var data: ArrayList<Client> = ArrayList(mList) // update the data property with mList
    var listener: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.clientvendeurrs, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.nomclient.text = "Nom : " + ItemsViewModel.name
        holder.email.text = "email : " + ItemsViewModel.email
        holder.telephone.text = "Numero Tel : " + ItemsViewModel.numeroTel
        holder.Adresse.text = "Adresse : " + ItemsViewModel.adresse
        holder.Mat_fiscale.text = "Matricule Fiscale : " + ItemsViewModel.Mat_fiscale







        // set click listener to view holder
        holder.itemView.setOnClickListener {
            listener?.invoke(ItemsViewModel._id)
        }

    }

    // add this for Filter (Search)
    fun setFilteredList(mList: List<Client>) {
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
        val nomclient: TextView = itemView.findViewById(R.id.nomclient)
        val email: TextView = itemView.findViewById(R.id.emailclient)
        val telephone: TextView = itemView.findViewById(R.id.numtelclient)
        val Adresse: TextView = itemView.findViewById(R.id.Adresselclient)
        val Mat_fiscale: TextView = itemView.findViewById(R.id.Matlclient)


    }
}