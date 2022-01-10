package com.example.vblood.view.activity
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vblood.R
import com.example.vblood.common.MyLocation
import com.example.vblood.databinding.ActivityDifferentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class DifferentLocationActivity : AppCompatActivity() {

    private lateinit var differentLocationBinding: ActivityDifferentLocationBinding
    private val REQUEST_LOCATION_PERMISSION=123
    private lateinit var map: GoogleMap
    //private var searchTextView = findViewById<TextView>(R.id.input_search)
    private var longitude:Double=0.0
    private var latitude:Double=0.0
    private var location:String =""



    private val callback = OnMapReadyCallback { googleMap ->

        map=googleMap
        enableMyLocation()
        val myLocation = LatLng(MyLocation.coords_y!!, MyLocation.coords_x!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15F))

    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        differentLocationBinding = ActivityDifferentLocationBinding.inflate(layoutInflater)
        setContentView(differentLocationBinding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.different_location_map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        differentLocationBinding.inputSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId==EditorInfo.IME_ACTION_SEARCH || actionId==EditorInfo.IME_ACTION_NEXT || actionId==EditorInfo.IME_ACTION_DONE){
                geoLocate()
            }
            false
        }

        differentLocationBinding.done.setOnClickListener {
            sendDataBackData()
        }

    }

    private fun sendDataBackData() {
        val intent = Intent().apply {
            putExtra("latitude", latitude)
            putExtra("longitude",longitude)
            putExtra("location",location)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun geoLocate() {

        val searchedString = differentLocationBinding.inputSearch.text.toString()

        val geocoder:Geocoder = Geocoder(this)
        var list:ArrayList<Address> = arrayListOf()
        try{
            list = geocoder.getFromLocationName(searchedString,1) as ArrayList<Address>

        }catch (e:IOException){
            Log.d("raj","IO EXCAEPTION")
        }

        if(list.size>0){
            val address=list.get(0)
            longitude=address.longitude
            latitude=address.latitude
            location=address.locality
            moveCamera(LatLng(address.latitude,address.longitude),address.getAddressLine(0))
        }

    }

    private fun moveCamera(lng: LatLng,title:String) {

        val zoom=15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lng,zoom))
        map.addMarker(
            MarkerOptions()
                .position(lng)
                .title(title))
    }
}