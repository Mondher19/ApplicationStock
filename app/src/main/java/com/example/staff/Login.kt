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
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the entered email is valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {

                        checkIfAdmin(it.getResult().user?.uid);


                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

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

                if(documentSnapshot.exists()) {

                    // Vérifier si admin
                    val isAdmin = documentSnapshot.getBoolean("role") ?: false

                    if(isAdmin) {

                       val intent = Intent(this, MainActivity::class.java)
                        val sharedPreferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)

                        sharedPreferences.edit().putString("name", documentSnapshot.getString("name").toString()).apply()
                        startActivity(intent)

                    } else {

                        val sharedPreferences = getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
                        sharedPreferences.edit().putString("name", documentSnapshot.getString("name").toString()).apply()

                        val intent = Intent(this, MainActivityAdmin::class.java)
                        startActivity(intent)
                    }

                } else {
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
