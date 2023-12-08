package com.example.staff

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
import com.example.staff.MainActivity
import com.example.staff.MainActivityAdmin
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

@Suppress("UNREACHABLE_CODE")
public class Login : AppCompatActivity() {


    private lateinit var emailInput: EditText
    private lateinit var editTextPassword: EditText
    lateinit var signupBtn: Button
    lateinit var buttonReg: Button
    private lateinit var auth: FirebaseAuth

    private lateinit var  fstore: FirebaseFirestore
    private val TAG = "LoginActivity"


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        emailInput = findViewById(R.id.emailadresse)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.signinBtn)


        buttonReg.setOnClickListener {
            val email = emailInput.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Veuillez entrer votre adresse e-maill !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the entered email is valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Veuillez entrer une adresse e-mail valide clie!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Veuillez entrer votre mot de passe !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {

                        checkIfAdmin(it.getResult().user?.uid);


                    } else {
                        Toast.makeText(this, "Connexion échouée: veuillez vérifier votre adresse email et mot de passe", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }


        }

    }

    private fun checkIfAdmin(uid: String?) {

        val userRef = uid?.let { fstore.collection("Users").document(it) }

        if (userRef != null) {
            userRef.get().addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {

                    // Get the document ID
                    val documentId = documentSnapshot.id

                    // Get role information
                    val role = documentSnapshot.getString("role") ?: ""

                    // Storing values into SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("user_id", documentId)
                        putString("name", documentSnapshot.getString("name").toString())
                        putString("role", role)
                        val latitude = documentSnapshot.getDouble("latitude") ?: 0.0
                        val longitude = documentSnapshot.getDouble("longitude") ?: 0.0

                        putFloat("latitude", latitude.toFloat())
                        putFloat("longitude", longitude.toFloat())
                        apply()
                    }

                    // Choose the appropriate activity based on the user role
                    val intent = when (role) {
                        "Magazinier" -> Intent(this, MainActivityMagazinier::class.java)
                        "Superviseur" -> Intent(this, MainActivityAdmin::class.java)
                        "Vendeur" -> Intent(this, MainActivity::class.java)
                        else -> Intent(this, MainActivity::class.java) // fallback if no roles match
                    }
                    startActivity(intent)
                }else {
                    // Document n'existe pas
                }

            }
        }

    }

    @Override
    override fun onStart() {
        super.onStart()

        if(auth.currentUser != null){
            checkIfAdmin(auth.currentUser!!.uid)
        }
    }

}
