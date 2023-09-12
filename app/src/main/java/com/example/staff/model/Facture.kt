package com.example.staff.model

import java.util.Date


data class ProductStock(
    val product: String, // Remplacez le type par le type approprié si besoin
    val quantity: Int
)

data class Facture(
    val _id: String,
    val client: String, // formulaire
    val products: List<ProductAllocation>, // formulaire
    val totalAmount: Double,
    val date: Date,
    val credit: Boolean, // formulaire
    val nomVendeur: String,
    val location: Location?,
    val qrCode: String?

)

data class ProductAllocation(
    val productId: String,
    val quantity: Int
)


data class addFacture(
    val clientId: String, // formulaire
    val products: List<ProductAllocation>, // formulaire
    val credit: Boolean, // formulaire
    val nomVendeur: String,
)

