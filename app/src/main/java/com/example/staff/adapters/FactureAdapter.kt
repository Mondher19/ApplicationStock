package com.example.staff.adapters

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import androidx.recyclerview.widget.RecyclerView
import com.example.staff.ModifierProduit
import com.example.staff.R
import com.example.staff.model.Facture
import com.example.staff.model.Journal
import com.example.staff.model.Produit
import com.example.staff.service.ApiHelper
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class FactureAdapter(private var mList: List<Journal>) : RecyclerView.Adapter<FactureAdapter.ViewHolder>() {

    var data: ArrayList<Journal> = ArrayList(mList) // update the data property with mList
    var listener: ((String) -> Unit)? = null
    var downloadListener: ((Uri) -> Unit)? = null
    var itemClickListener: ((String) -> Unit)? = null // define a lambda function

    fun setOnClickListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.facturersadmin, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val factureItem = mList[position]
        holder.nomclient.text = "Client: " + factureItem.clientName
        holder.nomvendeur.text =  "Vendeur: " + factureItem.vendeurName
        holder.totalamount.text =  "Total : " + factureItem.totalAmount + "TND"

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(factureItem._id) // Call the lambda function when the item is clicked
        }


        holder.datefacture.text = factureItem.dateHeure
        holder.refrencefacture.text =  "Ref : " + factureItem._id


        holder.dotsbtn.setOnClickListener { view ->
            // Create and show a PopupMenu
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.facture_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_modify -> {
                        Log.d("DownloadPDF", "Starting download process...")
                        val dynamicFileName = factureItem.invoicePDF ?: ""

                        if (dynamicFileName.isNotEmpty()) {
                            // Using Firebase Storage to get the download URL
                            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://nomadi-fbad2.appspot.com/")
                            val pdfRef = storageRef.child(dynamicFileName)

                            pdfRef.downloadUrl.addOnSuccessListener { uri ->
                                // Now we have an HTTP URL, suitable for DownloadManager
                                val downloadManager = view.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                val request = DownloadManager.Request(uri)
                                    .setTitle(factureItem.invoicePDF)
                                    .setDescription("Downloading")
                                    .setMimeType("application/pdf") // Optional: Set MIME type if known
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, factureItem.invoicePDF)
                                    .setAllowedOverMetered(true)
                                    .setAllowedOverRoaming(true)

                                // Start the download
                                val downloadID = downloadManager.enqueue(request)
                            }.addOnFailureListener {
                                // Handle any errors here
                                Log.e("DownloadPDF", "Download failed: ${it.message}")
                            }
                        } else {
                            Log.e("DownloadPDF", "File name is empty. Cannot download.")
                            // TODO: Provide user feedback that the file name is empty.
                        }
                        true
                    }
                    R.id.action_delete -> {
                        // Show confirmation dialog
                        AlertDialog.Builder(holder.itemView.context)
                            .setTitle("Confirmation")
                            .setMessage("Êtes-vous sûr de vouloir supprimer ce facture?")
                            .setPositiveButton("Oui") { _, _ ->
                                // Delete the product if 'Yes' is clicked
                                ApiHelper().deleteJournalFromApi(factureItem._id) {
                                    Toast.makeText(holder.itemView.context, "facture Supprimer", Toast.LENGTH_SHORT).show()
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
            listener?.invoke(factureItem.dateHeure.toString())
        }

    }

    // add this for Filter (Search)
    fun setFilteredList(mList: List<Journal>) {
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
        val nomvendeur: TextView = itemView.findViewById(R.id.Nomvendeur)

        val datefacture: TextView = itemView.findViewById(R.id.datefacture)
        val refrencefacture: TextView = itemView.findViewById(R.id.RefFacture)
        val totalamount: TextView = itemView.findViewById(R.id.totalamount)


        val dotsbtn: ImageButton = itemView.findViewById(R.id.FactureBtn)


    }
}