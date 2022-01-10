package com.example.vblood.view.activity
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.databinding.ActivityAddRequestBinding
import com.example.vblood.model.Request
import com.example.vblood.util.Constants
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.vblood.common.MyLocation
import com.example.vblood.model.User
import com.example.vblood.model.NotificationData
import com.example.vblood.model.PushNotification
import com.example.vblood.util.Constants.topic
import com.example.vblood.util.OfflineData

class AddRequestActivity : AppCompatActivity() {
    private lateinit var addRequestBinding: ActivityAddRequestBinding
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }

    private lateinit var bloodGroup:String
    private lateinit var state:String
    private lateinit var requestMessage:String
    private var isPlasma:Boolean=false
    private lateinit var user: User
    private var latitude:Double?=MyLocation.coords_x
    private var longitude:Double?=MyLocation.coords_y

    companion object {
        const val START_ACTIVITY_3_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        addRequestBinding = ActivityAddRequestBinding.inflate(layoutInflater)
        setContentView(addRequestBinding.root)
        addRequestBinding.requestBloodGroupToggleGroup.setOnSelectListener {
            bloodGroup = it.text
        }

        addRequestBinding.requestPlasmaOrBloodToggleGroup.setOnSelectListener {
            if(it.text=="false") isPlasma=false
            else if(it.text=="true") isPlasma=true
        }

        vBloodViewModel.userDetail.observe(this, {
            it?.let {
                user = it
            }
        })

        val coordinates="${MyLocation.coords_x},${MyLocation.coords_y}"
        val location=MyLocation.location_string+" ("+coordinates+")"
        addRequestBinding.requestLocationGotTextView.text = location

        addRequestBinding.requestFindDonorButton.setOnClickListener {
            if (checkForInput() ) {
                LoadingDialog.showLoadingDialogBox(true,this)
                LoadingDialog.changeText(this,"Registering Request")
                val formatter = SimpleDateFormat("dd/mm/yy HH:mm a", Locale.getDefault())
                val now = Date()
                val time = formatter.format(now)
                requestMessage=addRequestBinding.requestMessageEditText.text.toString()

                val request = Request(name = user.name,phoneNo = user.phoneNo, isPlasma = isPlasma,bloodGroup = bloodGroup, time = time,location_x = latitude!!,location_y = longitude!!,requestMessage = requestMessage,isBooked = false)
                vBloodViewModel.requestForDonor(request)
            }
        }

        vBloodViewModel.requestDonorError.observe(this,{
            it?.let{
                LoadingDialog.showLoadingDialogBox(false,this)
                if(it==Constants.state_s){
                    notificationForPotentialDonor()
                    Toast.makeText(this,"Request Made",Toast.LENGTH_SHORT).show()
                    vBloodViewModel.requestDonorError.postValue(null)
                    finish()
                }
                else{
                    Toast.makeText(this,"Unable To Make Request",Toast.LENGTH_SHORT).show()
                    resetValues()
                }
            }
        })

        addRequestBinding.requestDifferentLocation.setOnClickListener {
            val intent = Intent(this, DifferentLocationActivity::class.java)
            startActivityForResult(intent, START_ACTIVITY_3_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_ACTIVITY_3_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val coords_x = data!!.getDoubleExtra("latitude",MyLocation.coords_x!!)
                val coords_y = data.getDoubleExtra("longitude",MyLocation.coords_y!!)
                latitude=coords_x
                longitude=coords_y
                val l = data.getStringExtra("location")
                val coordinates="${coords_x},${coords_y}"
                val location= "$l ($coordinates)"
                addRequestBinding.requestLocationGotTextView.text = location
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun notificationForPotentialDonor() {
        //bloodGroup

        state=user.state
        val TOPIC="/topics/$state/${Constants.getBloodGroupInWords(bloodGroup)}"
        Log.d("raj","Notification Send: $TOPIC")
        PushNotification(
            NotificationData(bloodGroup,requestMessage,MyLocation.coords_x!!,MyLocation.coords_y!!),
            TOPIC
        ).also{
            vBloodViewModel.sendNotification(it)
        }
    }

    private fun resetValues() {

    }

    private fun checkForInput(): Boolean {
        if (MyLocation.coords_x != null && MyLocation.coords_y != null)
            return true
        return false
    }
}