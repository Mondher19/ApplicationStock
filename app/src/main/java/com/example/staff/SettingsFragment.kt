package com.example.staff

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ServiceCast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(    R.layout.fragment_settings, container, false)

        val fragmentManager = requireFragmentManager()

        val editProfileCard: CardView = view.findViewById(R.id.editprofilecard)
        val notificationCard: CardView = view.findViewById(R.id.Notification)
        val privatePolicyCard: CardView = view.findViewById(R.id.PrivatePolicy)
        val languagesCard: CardView = view.findViewById(R.id.Languages)
        val logoutCard: CardView = view.findViewById(R.id.Logout)
        lateinit var auth: FirebaseAuth

        var switch = view.findViewById<SwitchCompat>(R.id.nightid)

        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
        val userfullnamefromshared = sharedPreferences.getString("name", null)
        switch.isChecked = sharedPreferences.getBoolean("night", null ==true)



        val fullName: TextView = view.findViewById(R.id.Username)
        fullName.setText(userfullnamefromshared)

        auth = FirebaseAuth.getInstance()



        logoutCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
            builder.setPositiveButton("Oui") { _, _ ->
                // user clicked "Yes", logout
                val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Activity.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                sharedPreferences.edit().putBoolean("stay_connected", false).apply()
                sharedPreferences.edit().putString("role", null).apply()
                FirebaseAuth.getInstance().signOut() // Corrected this line

                val intent = Intent(activity, Login::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("No") { _, _ ->
                // user clicked "No", do nothing
            }
            val dialog = builder.create()
            dialog.show()
        }
        val notificationSwitch : SwitchCompat = view.findViewById(R.id.switchnotification)
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable notifications
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create a notification channel if the device runs Android 8.0 or higher
                    val channel = NotificationChannel(
                        "default",
                        "Default",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Notifications Enabled")
                alertDialogBuilder.setMessage("Notifications have been enabled.")
                alertDialogBuilder.setPositiveButton("OK") { _, _ -> }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            } else {
                // Disable notifications
                notificationManager.cancelAll()
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Notifications Disabled")
                alertDialogBuilder.setMessage("Notifications have been disabled.")
                alertDialogBuilder.setPositiveButton("OK") { _, _ -> }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }






        val uiModeManager = requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val night = sharedPreferences.getBoolean("night", null == true)


        if (night == true) {
            // Night mode enabled
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("night", true).apply()

        } else  {
            // Night mode disabled
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
            sharedPreferences.edit().putBoolean("night", false).apply()
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == true) {
                // Night mode enabled
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                sharedPreferences.edit().putBoolean("night", true).apply()

            } else  {
                // Night mode disabled
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                sharedPreferences.edit().putBoolean("night", false).apply()
            }
        }

        return view
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
