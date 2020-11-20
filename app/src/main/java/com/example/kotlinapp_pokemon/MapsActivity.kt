package com.example.kotlinapp_pokemon

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private val PERMISSION_ID = 1010
    private lateinit var mMap: GoogleMap
    var listOfPockemons = ArrayList<Pockemon>()
    lateinit var myLocation:Location
    private var TAG:String ="Debug"
    var oldLocation:Location?=null
    var myPower:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        RequestPermission()
        getLastLocation()
        LoadPockemons()

    }


    fun RequestPermission(){

        Log.d(TAG, "RequestPermission 1: ")
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        Log.d(TAG, "getLastLocation 2: ")
        if(CheckPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                    myLocation= task.result
                    if(myLocation == null){
                        NewLocationData()
                    }else{
                        var myThread = MyThread()
                        myThread.start()
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location (gps)",Toast.LENGTH_SHORT).show()


            }
        }else{
            RequestPermission()
        }
    }

    private fun CheckPermission():Boolean{
        Log.d(TAG, "CheckPermission 3: ")
        if(
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        Log.d(TAG, "onRequestPermissionsResult: ")
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //           Log.d("Debug:","You have the Permission")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: ")
        mMap = googleMap
    }

    @SuppressLint("MissingPermission")
    fun NewLocationData(){
        Log.d(TAG, "NewLocationData 5 : ")
        println("it entered")

        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }



    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
     //       Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
        }
    }

    fun isLocationEnabled():Boolean{
        Log.d(TAG, "isLocationEnabled 4: ")
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
     //   Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

    inner class MyThread : Thread {
        constructor() : super() {
            oldLocation = Location("oldLocation")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }

        override fun run() {
            // to update my location on the map continuously
            while (true) {
                try {
                    //this condition only to update my location on the map only when my oldLocation change
                    if (oldLocation==myLocation){
                        continue
                    }

                    oldLocation=myLocation

                    // runUiThread this is used to able to access any xml ui from inside the Thread class
                    runOnUiThread {
                        mMap!!.clear()

                        var TAG: String = "checkLoc"
                        Log.d(TAG, "lat: " + myLocation!!.latitude + " log " + myLocation!!.longitude)
                        // Add a marker in Sydney and move the camera
                        val sydney = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                        mMap!!.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet("here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.download0))
                        )
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 6.5f))

                        for (i in 0..listOfPockemons.size - 1) {
                            var newPockemon = listOfPockemons[i]

                            if (newPockemon.isCatch == false) {
                                val pockLocation = LatLng(
                                    newPockemon.location!!.latitude,
                                    newPockemon.location!!.longitude
                                )
                                mMap!!.addMarker(
                                    MarkerOptions()
                                        .position(pockLocation)
                                        .title(newPockemon.name)
                                        .snippet(newPockemon.des)
                                        .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!))
                                )

                                if(myLocation.distanceTo(newPockemon.location)<2){
                                    myPower+=newPockemon.power!!
                                    newPockemon.isCatch=true
                                    listOfPockemons[i]=newPockemon
                                    Toast.makeText(applicationContext,"You chach a new pockemon, your new power is"+myPower,
                                    Toast.LENGTH_LONG).show()

                                }
                            }
                        }

                    }

                    Thread.sleep(1000)
                } catch (ex: Exception) {
                }
            }
        }

    }


    fun LoadPockemons() {
        listOfPockemons.add(Pockemon( R.drawable.download1,
            "Charmander", "Charmander is living in japan", 55.0,26.7949878,32.401887879))
        listOfPockemons.add(Pockemon( R.drawable.download2,
            "Bulbasure", "Bulbasure is living in usa", 90.5,25.79491565,29.401889848))
        listOfPockemons.add(Pockemon( R.drawable.download3,
            "Squiertle", "Squiertle is living in iraq", 33.5,27.79491516,30.4018153222232))
    }


}

