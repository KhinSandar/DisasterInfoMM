package com.disasterinfomm

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.location.Location
import android.Manifest
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // Add a marker and move the camera
        enableMyLocation()
        loadMarkers()

        gMap.setOnMapClickListener { latLng ->
            showMarkerDialog(latLng)
        }

        // Handle marker clicks to delete them
        gMap.setOnMarkerClickListener { marker ->
            showDeleteMarkerDialog(marker)
            true
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            gMap.isMyLocationEnabled = true
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val myLocation = LatLng(location.latitude, location.longitude)
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                gMap.addMarker(MarkerOptions().position(myLocation).title("You are here"))
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    private fun showMarkerDialog(latLng: LatLng) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Action")
        builder.setMessage("Do you want to mark this location?")
        builder.setPositiveButton("Mark Here") { _, _ ->
            addMarker(latLng)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun addMarker(latLng: LatLng) {
        val marker = gMap.addMarker(MarkerOptions().position(latLng).title("Marker at ${latLng.latitude}, ${latLng.longitude}"))
        marker?.let {
            markers.add(it)
            saveMarkers() // Save markers after adding
        }
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }

    private fun showDeleteMarkerDialog(marker: Marker) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Marker")
        builder.setMessage("Do you want to delete this marker?")
        builder.setPositiveButton("Delete") { _, _ ->
            removeMarker(marker)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun removeMarker(marker: Marker) {
        marker.remove()
        markers.remove(marker)
        saveMarkers() // Save markers after removing
    }

    private fun saveMarkers() {
        val sharedPreferences = getSharedPreferences("Markers", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val markerList = markers.map { marker ->
            "${marker.position.latitude},${marker.position.longitude}"
        }
        editor.putStringSet("marker_positions", markerList.toSet())
        editor.apply()
    }

    private fun loadMarkers() {
        val sharedPreferences = getSharedPreferences("Markers", MODE_PRIVATE)
        val markerSet = sharedPreferences.getStringSet("marker_positions", null)

        markerSet?.forEach { markerData ->
            val latLng = markerData.split(",")
            val latitude = latLng[0].toDouble()
            val longitude = latLng[1].toDouble()
            addMarker(LatLng(latitude, longitude))
        }
    }
}
