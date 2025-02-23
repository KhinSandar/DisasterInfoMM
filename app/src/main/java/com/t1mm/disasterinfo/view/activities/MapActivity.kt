package com.t1mm.disasterinfo.view.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.t1mm.disasterinfo.R
import com.t1mm.disasterinfo.model.models.ShelterLocation

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    //creating GoogleMap object
    private lateinit var mMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment

        //checking MapFragment is existed or not
        if(mapFragment != null){
            mapFragment.getMapAsync(this)
        } else {
            Log.e("MapActivity", "Map fragment not found!")
            Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_LONG).show()
        }

        //Zoom click event
        val zoomInBtn = findViewById<TextView>(R.id.zoom_in_btn)
        val zoomOutBtn = findViewById<TextView>(R.id.zoom_out_btn)

        zoomInBtn.setOnClickListener{
            mMap.moveCamera(CameraUpdateFactory.zoomIn())
        }

        zoomOutBtn.setOnClickListener{
            mMap.moveCamera(CameraUpdateFactory.zoomOut())
        }

    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Create own mark for the custom location
        //16.799697445435598, 96.14779732646304 -> Shelter_1
        //Lat: 16.793228, 96.130086 -> Shelter_2
        //16.854981, 96.205829 -> Shelter_3
//        val customLocation = LatLng(16.7996, 96.1477)

        //We have to specify every shelter location in database
        val shelter_locations = mutableListOf<ShelterLocation>()
        shelter_locations.add(ShelterLocation("Shelter_1", 16.7996, 96.1477))
        shelter_locations.add(ShelterLocation("Shelter_2", 16.7932, 96.1300))
        shelter_locations.add(ShelterLocation("Shelter_3", 16.8549, 96.2058))

        //nullable variable to store each location
        var customLocation: LatLng? = null
        //Making shelter's icon
        val markerIcon = BitmapFactory.decodeResource(resources, R.drawable.shelter)
        val resizedIcon = Bitmap.createScaledBitmap(markerIcon,100,100,false)

        shelter_locations.forEach{ location ->

            customLocation = LatLng(location.lat, location.lng)
            Log.d("Shelter Location: ", customLocation!!.toString())
            val marker = mMap.addMarker(
                //Showing each shelter location
                MarkerOptions()
                    .position(customLocation!!)
                    .title(location.name.toString())
                    .icon(BitmapDescriptorFactory.fromBitmap(resizedIcon))
            )

            //Show title
            marker?.showInfoWindow()
        }


        //Focus zoom on the last updated shelter Location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customLocation!!, 12f))

        //Getting LatLng from the tapped location
        mMap.setOnMapClickListener { latLng ->
            val tappedLocation = getTappedLocation(latLng)
            Log.d("MyLocation", "Latitute: ${tappedLocation.longitude}, Longitude: ${tappedLocation.longitude}")
            //println("Tapped Location: Latitude = ${tappedLocation.latitude}, Longitude = ${tappedLocation.longitude}")
            Toast.makeText(this, "Lat: ${tappedLocation.latitude} \n Lng: ${tappedLocation.longitude}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getTappedLocation(latLng: LatLng): LatLng {
        mMap.addMarker(
            MarkerOptions().position(latLng).title("your location")
        )
        return latLng
    }
}
