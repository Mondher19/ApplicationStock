package com.example.staff.service

import com.example.staff.model.Client
import com.example.staff.model.Clientadd
import com.example.staff.model.Facture
import com.example.staff.model.Journal
import com.example.staff.model.ProductAllocationBody
import com.example.staff.model.Produit
import com.example.staff.model.Produitadd
import com.example.staff.model.Vendeur
import com.example.staff.model.addFacture
import com.example.staff.model.products
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface apientities {


    @GET("/client/clients")
    fun getclients(): Call<List<Client>>

    @GET("/facture/factures")
    fun getfactures(): Call<List<Facture>>

    @GET("/produit/journals")
    fun getjournals(): Call<List<Journal>>

    @DELETE("/produit/journals/{id}")
    fun deletejournal(@Path("id") type: String): Call<Journal>



    @GET("/produit/journals/{vendorName}")
    fun getJournalByVendorName(@Path("vendorName") vendorName: String): Call<List<Journal>>

    @GET("/vendeur/vendeur/{vendorName}")
    fun getvendeurByVendorName(@Path("vendorName") vendorName: String): Call<List<Vendeur>>

    @GET("/vendeur/vendeurs")
    fun getvendeurs(): Call<List<Vendeur>>

    @DELETE("/client/clients/{id}")
    fun deleteclient(@Path("id") type: String): Call<Client>

    @GET("/produit/produits/{id}")
    fun getproduit(@Path("id") type: String): Call<Produit>

    @GET("/produit/vendeur/{id}/stock")
    fun getVendeurStock(@Path("id") vendeurId: String): Call<List<products>>

    @PUT("/produit/vendeurs/{id}/validate")
    fun validateVendeur(@Path("id") vendeurId: String, @Body productAllocations: ProductAllocationBody): Call<List<products>>

    @POST("/client/clients")
    fun addclient(@Body clientadd: Clientadd): Call<Clientadd>

    @PUT("/client/clients/{id}")
    fun updateclient(@Path("id") type: String,@Body clientadd: Clientadd): Call<Clientadd>

    @PUT("/produit/vendeurs/{id}/produits")
    suspend fun allocateproducts(@Path("id") type: String, @Body productAllocations: ProductAllocationBody): Response<List<products>>


    @GET("/produit/produits")
    fun getproduits(): Call<List<Produit>>


    @DELETE("/produit/produits/{id}")
    fun deleteproduit(@Path("id") type: String): Call<Produit>


    @POST("/produit/produits")
    suspend fun addproduit(@Body produitadd: Produitadd): Response<Produitadd>

    @POST("/facture/factures")
    fun addfacture(@Body facture: addFacture): Call<addFacture>

    @PUT("/produit/produits/{id}")
    fun updateproduit(@Path("id") type: String,@Body produitadd: Produitadd): Call<Produitadd>

    @PATCH("/vendeur/vendeurs/{id}")
    fun updateVendeur(@Path("id") id: String, @Body updateData: Map<String, Float>): Call<Vendeur>

    @PATCH("/vendeur/vendeurs0/{id}")
    fun resetVendeurCredit(@Path("id") id: String): Call<Vendeur>

}