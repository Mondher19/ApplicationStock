package com.example.staff.model

data class Produit(
    val _id: String,
    val nom: String,
    val description: String,
    val prix: String,
    val stock: String,
)


data class Produitadd(
    val nom: String,
    val description: String,
    val prix: String,
    val stock: String,
)
