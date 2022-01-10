package com.example.vblood.view.activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.common.MyLocation
import com.example.vblood.databinding.ActivityHomeBinding
import com.example.vblood.util.Constants
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var donorHomeBinding: ActivityHomeBinding
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSION_ID = 1010


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {



        val bloodGroup=OfflineData(this).getUserBloodGroup()
        val isDonor=OfflineData(this).getUserIsDonor()
        val state=OfflineData(this).getUserState()

        val TOPIC="/topics/$state/${Constants.getBloodGroupInWords(bloodGroup!!)}"


        Log.d("raj","Notification Received: $TOPIC")
        super.onCreate(savedInstanceState)
        donorHomeBinding= ActivityHomeBinding.inflate(layoutInflater)
        donorHomeBinding.donorBottomNavView.setBackgroundColor(Color.parseColor("#B62637"))
        setContentView(donorHomeBinding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val clicked = intent.getBooleanExtra("clicked",false)

        if(clicked){
            Log.d("raj","clicked on notification")
            //Toast.makeText(this, "Open All Notifications $TOPIC",Toast.LENGTH_SHORT).show()
        }
        else Log.d("raj","message_empty")

        if(isDonor) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener {
                Log.d("raj", "Successfully subscribed")
            }
        }

        initialiseBottomNav()

        LoadingDialog.showLoadingDialogBox(true,this)

        getUserDetail()

        getUserRequest()

        vBloodViewModel.userDetail.observe(this,{
            it?.let{
                OfflineData(this).putUserIsDonor(it.isDonor)
                OfflineData(this).putUserBloodGroup(it.bloodGroup)
                OfflineData(this).putUserState(it.state)
                vBloodViewModel.myRequestData.observe(this,{ allRequests->
                    allRequests?.let{
                        LoadingDialog.changeText(this,"Getting Users Location")
                        RequestPermission()
                        getLastLocation()
                    }
                })
            }
        })

    }

    private fun getUserRequest() {
        LoadingDialog.changeText(this,"Getting Users Requests")
        val bloodGroup=OfflineData(this).getUserBloodGroup()
        if(bloodGroup!=null) vBloodViewModel.getRequestDataForDonor(bloodGroup)
    }

    private fun getUserDetail() {
        LoadingDialog.changeText(this,"Getting User Detail")
        vBloodViewModel.getUser()
    }

    private fun initialiseBottomNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(com.example.vblood.R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        donorHomeBinding.donorBottomNavView.setupWithNavController(navController)
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    val location: Location? = task.result
                    if(location != null){
                        MyLocation.coords_x=location.longitude
                        MyLocation.coords_y=location.latitude
                        MyLocation.location_string=getCityName(location.latitude,location.longitude)
                        LoadingDialog.showLoadingDialogBox(false,this)
                    }
                    else{
                        Toast.makeText(this,"Location is Null",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                 Toast.makeText(this,"Please turn on your device location and restart", Toast.LENGTH_LONG).show()
                 //LoadingDialog.changeText(this,"Turn On Location Manually And Restart")
                 finish()
            }
        }else{
            RequestPermission()
        }
    }



    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    private fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("raj","You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        val cityName: String
        val countryName: String
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, long, 3)

        cityName = address[0].locality
        countryName = address[0].countryName
        return "$cityName,$countryName"
    }

}