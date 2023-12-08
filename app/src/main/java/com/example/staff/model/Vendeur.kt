package com.example.staff.model

data class Vendeur(
    val _id: String,
    val nom: String,
    val validation: Boolean,
    val stock: List<products>,
    val progression: Number,
    var credit: Float,
)




data class products(

    val product: String,
    val quantite: Int,
    var productName: String
)

data class ProductAllocationBody(
    val products: List<products>)
