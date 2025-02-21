package com.t1mm.disasterinfo.view.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.t1mm.disasterinfo.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val firebaseApp = FirebaseApp.getInstance()
        Log.d("Firebase app", "Firebase installation completed")
        // Initialize Firebase Auth
        var auth: FirebaseAuth = Firebase.auth
        Log.d("Firebase auth", FirebaseAuth.getInstance().uid.toString())

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // Add a marker and move the camera
        val location = LatLng(-34.0, 151.0)
        gMap.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(location))

    }
}
