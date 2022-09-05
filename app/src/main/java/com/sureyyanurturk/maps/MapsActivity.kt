package com.sureyyanurturk.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sureyyanurturk.maps.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)
        // ,
        val of = LatLng(40.8817768, 40.1927689)
        mMap.addMarker(MarkerOptions().position(of).title("Of Cumhuriyeti"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(of,10f))


        locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener= object :LocationListener{
            override fun onLocationChanged(p0: Location) {
                mMap.clear()
                val currentMap = LatLng(p0.latitude,p0.longitude)
                mMap.addMarker(MarkerOptions().position(currentMap).title("Current Map"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentMap,10f))

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {

                   val adressList = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (adressList.size>0){
                        println(adressList[0].toString())
                    }

                }catch (e:Exception){e.printStackTrace()}
            }

        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val sonBilinenKonum= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null){
                val sonBilinenLatLng= LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLatLng).title("Son Bilinen Konum"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,10f))
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
            mMap.clear()

            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())

            if(p0 !=null){

                var adress =""
                try {
                    val adressList= geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (adressList.size>0){
                        if(adressList[0].thoroughfare != null){
                           adress += adressList[0].thoroughfare

                        }

                    }

                }catch (e: Exception){e.printStackTrace()}

                mMap.addMarker(MarkerOptions().position(p0).title(adress))
            }
        }
    }
}