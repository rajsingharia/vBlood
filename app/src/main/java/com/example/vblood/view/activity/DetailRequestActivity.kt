package com.example.vblood.view.activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.databinding.ActivityDetailRequestBinding
import com.example.vblood.util.Constants
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory

class DetailRequestActivity : AppCompatActivity() {
    private lateinit var detailRequestBinding: ActivityDetailRequestBinding
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailRequestBinding= ActivityDetailRequestBinding.inflate(layoutInflater)
        setContentView(detailRequestBinding.root)
        val id=intent.getStringExtra("id")

        if(id!=null) {
            getDetailAndSet(id)
            detailRequestBinding.detailRequestAccept.setOnClickListener {
                LoadingDialog.showLoadingDialogBox(true,this)
                LoadingDialog.changeText(this,"Booking")
                val bloodGroup=OfflineData(this).getUserBloodGroup()!!
                vBloodViewModel.bookRequestDataForDonor(id,bloodGroup)
                vBloodViewModel.isBookedError.observe(this,{
                    it?.let{
                        if(it==Constants.state_s){
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
        else {
            Toast.makeText(this,"Data Not Found",Toast.LENGTH_SHORT).show()
            finish()
        }

        detailRequestBinding.detailRequestPhoneEditText.setOnClickListener {
            checkPermission()
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
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detailRequestBinding.detailRequestPhoneEditText.text.toString()))
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            }
            return
        }
    }

    private fun getDetailAndSet(id:String) {
        val data =vBloodViewModel.othersRequestData.value
        if(data!=null){
            for(child in data){
                if(child.id==id){
                    detailRequestBinding.detailRequestBloodGroup.text=child.bloodGroup
                    detailRequestBinding.detailRequestNameEditText.setText(child.name)
                    detailRequestBinding.detailRequestPhoneEditText.setText(child.phoneNo)
                    detailRequestBinding.detailRequestMsgEditText.setText(child.requestMessage)
                    detailRequestBinding.detailRequestTime.text = child.time

                    if(child.isBooked){
                        detailRequestBinding.detailRequestAccept.visibility= View.INVISIBLE
                    }
                    else{
                        detailRequestBinding.detailRequestAccept.visibility= View.VISIBLE
                    }
                    break
                }
            }
        }
    }
}