
/*
package com.example.doctorcode


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permission
import java.security.Provider
import java.util.*

class MainActivity : AppCompatActivity() {

    //Declaring the needed Variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getpos.setOnClickListener {
            Log.d("Debug:",CheckPermission().toString())
            Log.d("Debug:",isLocationEnabled().toString())
            RequestPermission()
            /* fusedLocationProviderClient.lastLocation.addOnSuccessListener{location: Location? ->
                 textView.text = location?.latitude.toString() + "," + location?.longitude.toString()
             }*/
            getLastLocation()
        }

    }


    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                    var location:Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
                        Locationtxt.text = "You Current Location is : Long: "+ location.longitude + " , Lat: " + location.latitude + "\n" + getCityName(location.latitude,location.longitude)
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }


    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
            Locationtxt.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
        }
    }

    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

}


*/



package com.example.kotlinapp_pokemon

class comments {
}

/*

package com.example.kotlinapp_pokemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val accessLocation = 123
    var myLocation: Location? = null
    var listOfPockemons = ArrayList<Pockemon>()
    var locationManager : LocationManager? = null

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        LoadPockemons()
        checkPermission()

    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    accessLocation
                )
                return
            }
        }
        getUserLocation()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when (requestCode) {
            accessLocation -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "location access now", Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun getUserLocation() {
        Toast.makeText(this, "location access now", Toast.LENGTH_LONG).show()

        val my2Location = MyLocationListener()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        //my2Location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 3f, my2Location)



        val myThread = MyThread()
        myThread.start()

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
    }


    inner class MyLocationListener : LocationListener {
        constructor() {
            myLocation = Location("me")
            myLocation!!.longitude = 0.0
            myLocation!!.latitude = 0.0
        }

        override fun onLocationChanged(location: Location?) {
            print("Entered hi")
            myLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }

    inner class MyThread : Thread {
        constructor() : super() {
            //to do
        }

        override fun run() {
            try {
                // runUiThread this is used to able to access any xml ui from inside the Thread class
                runOnUiThread {
                    mMap!!.clear()

                    var TAG:String = "checkLoc"
                    Log.d(TAG, "lat: "+myLocation!!.latitude+" log "+ myLocation!!.longitude)
                    // Add a marker in Sydney and move the camera
                    val sydney = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                    mMap!!.addMarker(
                        MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet("here is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.download0))
                    )
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,3f))

                    for(i in 0..listOfPockemons.size-1){
                        var newPockemon = listOfPockemons[i]

                        if (newPockemon.isCatch==false){
                            val pockLocation = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                            mMap!!.addMarker(
                                MarkerOptions()
                                    .position(pockLocation)
                                    .title(newPockemon.name)
                                    .snippet(newPockemon.des)
                                    .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!))
                            )
                        }
                    }

                }

                Thread.sleep(1000)
            } catch (ex: Exception) {
            }
        }

    }

    fun LoadPockemons() {
        listOfPockemons.add(Pockemon( R.drawable.download1,
            "Charmander", "Charmander is living in japan", 55.0,37.7949878,-122.401887879))
        listOfPockemons.add(Pockemon( R.drawable.download2,
            "Bulbasure", "Bulbasure is living in usa", 90.5,50.79491565,-100.401889848))
        listOfPockemons.add(Pockemon( R.drawable.download3,
            "Squiertle", "Squiertle is living in iraq", 33.5,20.79491516,-50.4018153222232))
    }

}



 */