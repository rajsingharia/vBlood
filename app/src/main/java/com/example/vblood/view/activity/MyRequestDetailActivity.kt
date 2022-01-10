package com.example.vblood.view.activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.vblood.R
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.common.MyLocation
import com.example.vblood.databinding.ActivityMyRequestDetailBinding
import com.example.vblood.util.Constants
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.example.vblood.viewmodel.vBloodViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MyRequestDetailActivity : AppCompatActivity() {

    private lateinit var myRequestDetailBinding: ActivityMyRequestDetailBinding
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }
    private val REQUEST_LOCATION_PERMISSION=123
    private lateinit var map: GoogleMap
    private var longitude:Double=0.0
    private var latitude:Double=0.0
    private var location:String =""
    private var theMapIsReady:MutableLiveData<Boolean> = MutableLiveData()



    private val callback = OnMapReadyCallback { googleMap ->
        map=googleMap
        enableMyLocation()
        theMapIsReady.postValue(true)
        val myLocation = LatLng(MyLocation.coords_y!!, MyLocation.coords_x!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15F))
        changeMapAppearance()
    }

    private fun changeMapAppearance() {
        val nightModeFlags = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json))
        }
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
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
        if (requestCode == 42) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            }
            return
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myRequestDetailBinding= ActivityMyRequestDetailBinding.inflate(layoutInflater)
        setContentView(myRequestDetailBinding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.my_request_accepted_map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
        val id=intent.getStringExtra("id")

        if(id!=null) {
            getDetailAndSet(id)
            val isBooked=intent.getBooleanExtra("isBooked",false)
            checkToShowAcceptFAB(isBooked,id)
        }
        else {
            Toast.makeText(this,"Data Not Found", Toast.LENGTH_SHORT).show()
            finish()
        }

        myRequestDetailBinding.detailRequestPhoneEditText.setOnClickListener {
            checkPermission()
        }

        theMapIsReady.observe(this,{
            if(latitude!=0.0 && longitude!=0.0){
                moveCamera(LatLng(latitude,longitude),location)
            }
        })
    }

    private fun checkToShowAcceptFAB(isBooked:Boolean,id:String) {
        if(!isBooked) myRequestDetailBinding.myRequestAcceptFloatingActionButton.visibility= View.VISIBLE

        myRequestDetailBinding.myRequestAcceptFloatingActionButton.setOnClickListener {
            LoadingDialog.showLoadingDialogBox(true,this)
            LoadingDialog.changeText(this,"Booking")
            val bloodGroup= OfflineData(this).getUserBloodGroup()!!

            vBloodViewModel.bookRequestDataForDonor(id,bloodGroup)

            vBloodViewModel.isBookedError.observe(this,{
                it?.let{
                    if(it== Constants.state_s){
                        LoadingDialog.showLoadingDialogBox(false,this)
                        finish()
                    }
                    else {
                        LoadingDialog.showLoadingDialogBox(false,this)
                    }
                }
            })
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + myRequestDetailBinding.detailRequestPhoneEditText.text.toString()))
        startActivity(intent)
    }

    private fun getDetailAndSet(id:String) {
        val data =vBloodViewModel.othersRequestData.value
        if(data!=null){
            for(child in data){
                if(child.id==id){
                    longitude=child.location_x
                    latitude=child.location_y
                    location=child.name
                    myRequestDetailBinding.detailRequestTime.text = child.time
                    myRequestDetailBinding.detailRequestNameEditText.setText(child.name)
                    myRequestDetailBinding.detailRequestPhoneEditText.setText(child.phoneNo)
                    myRequestDetailBinding.detailRequestMsgEditText.setText(child.requestMessage)
                    break
                }
            }
        }
    }
    private fun moveCamera(lng: LatLng,title:String) {

        val zoom=15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, zoom))
        map.addMarker(
            MarkerOptions()
                .position(lng)
                .title(title)
        )
    }
}