package com.t1mm.disasterinfo.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.t1mm.disasterinfo.R

class RemoteConfigActivity: AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.remote_config)

        findViewById<Button>(R.id.fetchButton).setOnClickListener { fetchWelcome()}

        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)

                if (configUpdate.updatedKeys.contains(WELCOME_MESSAGE_KEY)) {
                    remoteConfig.activate().addOnCompleteListener {
                        displayWelcomeMessage()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })

        fetchWelcome()

    }

    private fun fetchWelcome() {
        findViewById<TextView>(R.id.welcomeTextView).text = remoteConfig[LOADING_PHRASE_CONFIG_KEY].asString()

        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(this, "Fetch and activate succeeded", Toast.LENGTH_SHORT,).show()
                } else {
                    Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT,).show()
                }
                displayWelcomeMessage()
            }
        // [END fetch_config_with_callback]
    }

    @SuppressLint("CutPasteId")
    private fun displayWelcomeMessage() {
        // [START get_config_values]
        val welcomeMessage = remoteConfig[WELCOME_MESSAGE_KEY].asString()
        // [END get_config_values]
        findViewById<TextView>(R.id.welcomeTextView).text = welcomeMessage
    }


    companion object {

        private const val TAG = "MainActivity"

        // Remote Config keys
        private const val LOADING_PHRASE_CONFIG_KEY = "loading_phrase"
        private const val WELCOME_MESSAGE_KEY = "welcome_message"
    }
    // [END display_welcome_message]
}