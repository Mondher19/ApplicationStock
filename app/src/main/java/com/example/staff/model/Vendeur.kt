package com.example.staff.model

data class Vendeur(
    val _id: String,
    val nom: String,
    val validation: Boolean,
    val stock: List<products>
)

data class products(
    val productId: String,    // This will store the ObjectID of the related Produit. If you need the actual product data, you should use Produit class instead of String.
    val quantite: Int
)

data class ProductAllocationBody(
    val products: List<products>)
