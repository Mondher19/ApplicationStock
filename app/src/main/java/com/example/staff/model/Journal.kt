package com.example.staff.model
import java.util.Date

data class Journal(
    val _id: String,
    val clientName: String,
    val invoicePDF: String, // This could be a path to the stored PDF file or a link to cloud storage
    val vendeurName: String,
    val dateHeure: String,
    val date: Date,
    val totalAmount: Double,
    val invoiceId: String? = null // Add this field to hold the identifier of the Facture
    // This could be null to represent the default "now" value
)