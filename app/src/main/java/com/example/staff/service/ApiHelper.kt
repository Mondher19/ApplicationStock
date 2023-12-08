package com.example.staff.service

import android.util.Log
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
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiHelper() {

    private val BASE_URL = "https://transportback.onrender.com/"

    private val apiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(apientities::class.java)


    //Get Functions//


    fun fetchclient( callback: (List<Client>) -> Unit) {
        val callUsers = apiService.getclients()
        callUsers.enqueue(object : Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                if (response.isSuccessful) {
                    callback(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                // handle API call failure here
            }
        })
    }


    fun fetchfacture( callback: (List<Facture>) -> Unit) {
        val callUsers = apiService.getfactures()
        callUsers.enqueue(object : Callback<List<Facture>> {
            override fun onResponse(call: Call<List<Facture>>, response: Response<List<Facture>>) {
                if (response.isSuccessful) {
                    callback(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Facture>>, t: Throwable) {
                // handle API call failure here
            }
        })
    }

    fun fetchjournal( callback: (List<Journal>) -> Unit) {
        val callUsers = apiService.getjournals()
        callUsers.enqueue(object : Callback<List<Journal>> {
            override fun onResponse(call: Call<List<Journal>>, response: Response<List<Journal>>) {
                if (response.isSuccessful) {
                    callback(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Journal>>, t: Throwable) {
                // handle API call failure here
            }
        })
    }


    fun fetchvendeur( callback: (List<Vendeur>) -> Unit) {
        val callUsers = apiService.getvendeurs()
        callUsers.enqueue(object : Callback<List<Vendeur>> {
            override fun onResponse(call: Call<List<Vendeur>>, response: Response<List<Vendeur>>) {
                if (response.isSuccessful) {
                    callback(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Vendeur>>, t: Throwable) {
                // handle API call failure here
            }
        })
    }



    fun deleteClientFromApi(Recid: String, callback: () -> Unit) {
        val callDelete = apiService.deleteclient(Recid)
        callDelete.enqueue(object : Callback<Client> {
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                if (response.isSuccessful) {
                    callback()
                }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                // handle API call failure here
            }
        })
    }

    fun deleteJournalFromApi(Recid: String, callback: () -> Unit) {
        val callDelete = apiService.deletejournal(Recid)
        callDelete.enqueue(object : Callback<Journal> {
            override fun onResponse(call: Call<Journal>, response: Response<Journal>) {
                if (response.isSuccessful) {
                    callback()
                }
            }

            override fun onFailure(call: Call<Journal>, t: Throwable) {
                // handle API call failure here
            }
        })
    }


    fun getJournalByVendorName(vendorName: String, callback: (List<Journal>?, String?) -> Unit) {
        Log.d("API_CALL", "Api call initiated for vendorName: $vendorName")  // Log when the call starts

        val call = apiService.getJournalByVendorName(vendorName)  // Make sure your apiService function also returns Call<List<Journal>>

        call.enqueue(object : Callback<List<Journal>> {
            override fun onResponse(call: Call<List<Journal>>, response: Response<List<Journal>>) {
                if (response.isSuccessful) {
                    Log.d("API_CALL", "Api call successful")  // Log for successful response

                    response.body()?.let { journalList ->
                        Log.d("API_CALL", "Received journals: $journalList")  // Log the data received
                        callback(journalList, null)
                    } ?: run {
                        Log.e("API_CALL", "Received null body in API response")  // Log for null body
                        callback(null, "Received null body in API response")
                    }
                } else {
                    val error = "Api call unsuccessful: ${response.errorBody()?.string()}"
                    Log.e("API_CALL", error)  // Log the unsuccessful response
                    callback(null, error)
                }
            }

            override fun onFailure(call: Call<List<Journal>>, t: Throwable) {
                val error = "Api call failed: ${t.message}"
                Log.e("API_CALL", error)  // Log for failure
                callback(null, error)
            }
        })
    }


    fun getVendeurByVendorName(vendorName: String, callback: (List<Vendeur>?, String?) -> Unit) {
        Log.d("API_CALL", "Api call initiated for vendorName: $vendorName")  // Log when the call starts

        val call = apiService.getvendeurByVendorName(vendorName)  // Make sure your apiService function also returns Call<List<Journal>>

        call.enqueue(object : Callback<List<Vendeur>> {
            override fun onResponse(call: Call<List<Vendeur>>, response: Response<List<Vendeur>>) {
                if (response.isSuccessful) {
                    Log.d("API_CALL", "Api call successful")  // Log for successful response

                    response.body()?.let { journalList ->
                        Log.d("API_CALL", "Received journals: $journalList")  // Log the data received
                        callback(journalList, null)
                    } ?: run {
                        Log.e("API_CALL", "Received null body in API response")  // Log for null body
                        callback(null, "Received null body in API response")
                    }
                } else {
                    val error = "Api call unsuccessful: ${response.errorBody()?.string()}"
                    Log.e("API_CALL", error)  // Log the unsuccessful response
                    callback(null, error)
                }
            }

            override fun onFailure(call: Call<List<Vendeur>>, t: Throwable) {
                val error = "Api call failed: ${t.message}"
                Log.e("API_CALL", error)  // Log for failure
                callback(null, error)
            }
        })
    }


    fun addClientToApi(clientadd: Clientadd, successCallback: () -> Unit, errorCallback: (String) -> Unit) {
        val callAdd = apiService.addclient(clientadd)
        callAdd.enqueue(object : Callback<Clientadd> {
            override fun onResponse(call: Call<Clientadd>, response: Response<Clientadd>) {
                if (response.isSuccessful) {
                    successCallback()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown Error"
                    errorCallback(errorMsg)
                }
            }

            override fun onFailure(call: Call<Clientadd>, t: Throwable) {
                errorCallback(t.localizedMessage ?: "Network error")
            }
        })
    }
    fun addFactureToApi(facture: addFacture, callback: (Result<addFacture>, String) -> Unit) {
        val callAdd = apiService.addfacture(facture)
        callAdd.enqueue(object : Callback<addFacture> {
            override fun onResponse(call: Call<addFacture>, response: Response<addFacture>) {
                if (response.isSuccessful) {
                    callback(Result.Success(response.body()!!), "Client added successfully")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    callback(Result.Error(Exception("API response error: $errorMsg")), errorMsg)
                }
            }

            override fun onFailure(call: Call<addFacture>, t: Throwable) {
                callback(Result.Error(t), t.message ?: "Network error")
            }
        })
    }

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: Throwable) : Result<Nothing>()
    }

    fun fetchproduit( callback: (List<Produit>) -> Unit) {
        val callProduits = apiService.getproduits()
        callProduits.enqueue(object : Callback<List<Produit>> {
            override fun onResponse(call: Call<List<Produit>>, response: Response<List<Produit>>) {
                if (response.isSuccessful) {
                    callback(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Produit>>, t: Throwable) {
                Log.e("API_CALL", "Error: ${t.message}")
            }
        })
    }

    fun fetchVendeurStock(vendeurId: String, callback: (List<products>?) -> Unit) {
        val callStock = apiService.getVendeurStock(vendeurId)
        callStock.enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                callback(response.body())
            }

            override fun onFailure(call: Call<List<products>>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun fetchseulproduit(produitid: String, callback: (Produit?) -> Unit) {
        val callProduits = apiService.getproduit(produitid)
        callProduits.enqueue(object : Callback<Produit> {
            override fun onResponse(call: Call<Produit>, response: Response<Produit>) {
                callback(response.body())
                if (!response.isSuccessful) {
                    Log.e("API_CALL", "Response unsuccessful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Produit>, t: Throwable) {
                Log.e("API_CALL", "Network Error: ${t.message}", t)
                callback(null)
            }
        })
    }

    fun deleteproduitFromApi(Recid: String, callback: () -> Unit) {
        val callDelete = apiService.deleteproduit(Recid)
        callDelete.enqueue(object : Callback<Produit> {
            override fun onResponse(call: Call<Produit>, response: Response<Produit>) {
                if (response.isSuccessful) {
                    callback()
                }
            }

            override fun onFailure(call: Call<Produit>, t: Throwable) {
                // handle API call failure here
            }
        })
    }



    suspend fun Allocateproducts(vendeurId: String, productAllocations: List<products>): Response<List<products>> {
        val body = ProductAllocationBody(productAllocations)

        return apiService.allocateproducts(vendeurId, body)
    }

    suspend fun addproduitToApi(produitadd: Produitadd): Response<Produitadd>? {
        return try {
            apiService.addproduit(produitadd)
        } catch (e: Exception) {
            null
        }
    }

    fun updateProduitToApi(productId: String, produitToUpdate: Produitadd, callback: () -> Unit) {
        val callUpdate = apiService.updateproduit(productId, produitToUpdate)
        callUpdate.enqueue(object : Callback<Produitadd> {
            override fun onResponse(call: Call<Produitadd>, response: Response<Produitadd>) {
                if (response.isSuccessful) {
                    callback()
                }
            }

            override fun onFailure(call: Call<Produitadd>, t: Throwable) {
                // handle API call failure here
            }
        })
    }

    fun validateVendeur(vendeurId: String, productAllocations: List<products>, callback: () -> Unit) {
        val body = ProductAllocationBody(productAllocations)

        Log.d("API_CALL", "Vendeur ID: $vendeurId")
        Log.d("API_CALL", "Product Allocations: $body")

        val callValidate = apiService.validateVendeur(vendeurId, body)
        callValidate.enqueue(object : Callback<List<products>> {
            override fun onResponse(call: Call<List<products>>, response: Response<List<products>>) {
                if (response.isSuccessful) {
                    callback()
                    callback.invoke()
                }
            }

            override fun onFailure(call: Call<List<products>>, t: Throwable) {
                // handle API call failure here
            }
        })
    }


    fun updateVendeurCredit(vendeurId: String, creditToAdd: Float, callback: () -> Unit) {
        val callUpdate = apiService.updateVendeur(vendeurId, mapOf("credit" to creditToAdd))
        callUpdate.enqueue(object : Callback<Vendeur> {
            override fun onResponse(call: Call<Vendeur>, response: Response<Vendeur>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    // Handle the case where the response is not successful, log the error message or inform the user
                    Log.e("API_CALL", "Failed to update Vendeur: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Vendeur>, t: Throwable) {
                // Handle API call failure here, log the error message or inform the user
                Log.e("API_CALL", "Failed to call API", t)
            }
        })
    }

    fun resetVendeurCredit(vendeurId: String, callback: () -> Unit) {
        val callUpdate = apiService.resetVendeurCredit(vendeurId)
        callUpdate.enqueue(object : Callback<Vendeur> {
            override fun onResponse(call: Call<Vendeur>, response: Response<Vendeur>) {
                if (response.isSuccessful) {
                    callback()
                } else {
                    Log.e("API_CALL", "Failed to reset Vendeur credit: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Vendeur>, t: Throwable) {
                Log.e("API_CALL", "Failed to call API", t)
            }
        })
    }


    fun updateClientToApi(clientId: String, clientToUpdate: Clientadd, callback: () -> Unit) {
        val callUpdate = apiService.updateclient(clientId, clientToUpdate)
        callUpdate.enqueue(object : Callback<Clientadd> {
            override fun onResponse(call: Call<Clientadd>, response: Response<Clientadd>) {
                if (response.isSuccessful) {
                    callback()
                }
            }

            override fun onFailure(call: Call<Clientadd>, t: Throwable) {
                // handle API call failure here
            }
        })
    }









}