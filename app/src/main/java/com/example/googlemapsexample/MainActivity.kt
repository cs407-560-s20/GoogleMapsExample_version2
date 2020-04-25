package com.example.googlemapsexample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder

    private val ACCESS_LOCATION_CODE = 123

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Request location permission and show user's current location on the Map
        getLocationPermission()

    }

    // Search a typed location using geocoder getFromLocationName method
    fun searchButton(view: View) {

        // Get address from the user
        val locationName = address_text.text.toString()
        address_text.hideKeyboard()
        //Log.d(TAG, "$locationName")

        if (locationName.isEmpty()){
            return
        }

        geocoder = Geocoder(this)

        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)

            if (addressList.size > 0){

                val address = addressList[0]
                Log.d(TAG, "$address")
                // the address looks like below:
                // Address[addressLines=[0:"Boston"],
                // feature=Boston,admin=Massachusetts,sub-admin=Suffolk County,locality=Boston,
                // thoroughfare=null,postalCode=null,countryCode=US,countryName=United States,
                // hasLatitude=true,latitude=42.3600825,hasLongitude=true,
                // longitude=-71.0588801,phone=null,url=null,extras=null]

                // Convert to latitude and latitude to LatLng
                val latLng = LatLng(address.latitude, address.latitude)

                // Set the marker options
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(address.locality)
                    .snippet("Interesting place!") // You can change this text to something else

                // Add the marker
                mMap.addMarker(markerOptions)

                // Move and animate the camera, 16f is the zoom level
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                mMap.animateCamera(cameraUpdate)

            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }

    }


    private fun getLocationPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            enableUserLocation()
        } else {

            // Permission is not granted
            // show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_LOCATION_CODE)

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_LOCATION_CODE)

                // ACCESS_LOCATION_CODE is an int constant (you decide a number). The callback method gets the
                // result of the request.
            }



        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_LOCATION_CODE){
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                enableUserLocation()
            }
            else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private fun enableUserLocation() {

        // Enable the show my location button on the Map
        mMap.isMyLocationEnabled = true
    }


    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }



}
