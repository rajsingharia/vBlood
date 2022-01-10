package com.example.vblood.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.vblood.R
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.MyLocation
import com.example.vblood.databinding.FragmentNotificationBinding
import com.example.vblood.model.Request
import com.example.vblood.util.OfflineData
import com.example.vblood.view.activity.AllRequestListActivity
import com.example.vblood.view.activity.DetailRequestActivity
import com.example.vblood.view.activity.MyRequestDetailActivity
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class NotificationFragment : Fragment() {

    private lateinit var notificationBinding: FragmentNotificationBinding
    private val REQUEST_LOCATION_PERMISSION=123
    private lateinit var map:GoogleMap
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((requireActivity().application as vBloodApplication).repository)
    }
    private var isMapMade=false

    private var requestMarkers:MutableList<Request> = mutableListOf()


    private val callback = OnMapReadyCallback { googleMap ->

        map=googleMap
        enableMyLocation()
        val myLocation = LatLng(MyLocation.coords_y!!, MyLocation.coords_x!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15F))

        changeMapAppearance()

        isMapMade=true
        setMarkers()

        googleMap.setOnMarkerClickListener {

            val snippet=it.snippet
            Log.d("raj",snippet!!.split("msg: ").toString())
            val msg=snippet!!.split("msg: ")[1]
            if(msg=="accepted"){
                val id=snippet.split("msg: ")[0]
                val intent= Intent(requireContext(),MyRequestDetailActivity::class.java)
                intent.putExtra("id",id)
                startActivity(intent)
            }
            else{
                val id=snippet.split("msg: ")[0]
                val intent= Intent(requireContext(),DetailRequestActivity::class.java)
                intent.putExtra("id",id)
                startActivity(intent)
            }
            false
        }

    }

    private fun changeMapAppearance() {
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json))
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationBinding= FragmentNotificationBinding.inflate(inflater,container,false)
        return notificationBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        notificationBinding.mapFab.setOnClickListener {
            startActivity(Intent(requireContext(),AllRequestListActivity::class.java))
        }

        vBloodViewModel.othersRequestData.observe(requireActivity(),{
            it?.let{
                requestMarkers=it
                if(isMapMade){
                    map.clear()
                    setMarkers()
                }
            }
        })
    }

    private fun setMarkers(){
        for(element in requestMarkers){
            val location = LatLng(element.location_y, element.location_x)
            if(map.isMyLocationEnabled && element.bloodGroup==OfflineData(requireActivity()).getUserBloodGroup()){
                Log.d("raj",element.location_x.toString()+","+element.location_y.toString())
                if(!element.isBooked) {
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(element.bloodGroup)
                            .snippet(element.id+"msg: "+element.requestMessage)
                            .icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_location_red))
                    )
                }
//                else{
//                    map.addMarker(
//                        MarkerOptions()
//                            .position(location)
//                            .title(element.bloodGroup)
//                            .snippet(element.id+"msg: accepted")
//                            .icon(bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_location_green))
//                    )
//                }
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}