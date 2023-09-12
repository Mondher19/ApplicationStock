package com.example.staff.model


data class Location(
    val latitude: Double,
    val longitude: Double
)

data class Client(
    val _id: String,
    val name: String,
    val numeroTel: String,
    val email: String?,
    val credit: Boolean,
    val qrCode: String?,
    val location: Location?,
    val facture: List<String>
)


data class Clientadd(
    val name: String,
    val numeroTel: String,
    var location: Location?,
    val email: String,
    val credit: Boolean,
    val qrCode: String?,
)
