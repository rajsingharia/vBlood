package com.example.vblood.view.activity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.vblood.R
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.databinding.ActivityRegisterBinding
import com.example.vblood.model.User
import com.example.vblood.util.Constants
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.github.drjacky.imagepicker.ImagePicker

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }

    private lateinit var stateSelected:String
    private lateinit var imageUri:Uri
    private lateinit var genderSelect:String
    private var isDonor:Boolean=false
    private lateinit var bloodGroup:String

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(this, R.layout.drop_down_item,Constants.states)
        registerBinding.stateSelectDropDownMenu.setAdapter(adapter)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        registerBinding.stateSelectDropDownMenu.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                stateSelected = Constants.states[position]
                registerBinding.stateSelectDropDownMenu.setText(stateSelected)
            }

        //LOGIN PAGE BUTTON

        registerBinding.loginAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //BACK BUTTON

        registerBinding.registerProfilePic.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .createIntentFromDialog { launcher.launch(it) }
        }

        registerBinding.genderToggleGroup.setOnSelectListener {
            genderSelect=it.text
        }

        registerBinding.donorSelectToggleGroup.setOnSelectListener {
            isDonor=true
        }

        registerBinding.bloodGroupToggleGroup.setOnSelectListener {
            bloodGroup=it.text
        }

        //REGISTRATION BUTTON

        registerBinding.registerButton.setOnClickListener {
            if(checkInput()){
                //TODO: Registration
                LoadingDialog.showLoadingDialogBox(true,this)
                LoadingDialog.changeText(this,"Registering The User")
                val email=registerBinding.registerEmailEditText.text.toString()
                val password=registerBinding.registerPasswordEditText.text.toString()
                vBloodViewModel.register(email,password)
                uploadImageAndData()
            }else {
                Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun uploadImageAndData() {
        vBloodViewModel.currentUser.observe(this,{
            it?.let {
                LoadingDialog.changeText(this,"Uploading UserProfile Picture")
                vBloodViewModel.uploadImage(imageUri)
            }
        })

        vBloodViewModel.uploadImageUrl.observe(this,{
            it?.let{
                Log.d("TEST","Image Uploaded")
                uploadData(it)
            }
        })


        vBloodViewModel.userState.observe(this,{
            it?.let {
                if(it==Constants.state_s){
                    Log.d("TEST","Data Uploaded")
                    OfflineData(this).putUserType("Donor")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(it==Constants.state_f){
                    Log.d("TEST","Data Unable Upload")
                    resetValues()
                    LoadingDialog.showLoadingDialogBox(false,this)
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
            }
        })



        vBloodViewModel.uploadImageUrlError.observe(this,{
            it?.let{
                if(it==Constants.state_f){
                    resetValues()
                    LoadingDialog.showLoadingDialogBox(false,this)
                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                }
            }
        })

        vBloodViewModel.error.observe(this,{
            it?.let{
                resetValues()
                LoadingDialog.showLoadingDialogBox(false,this)
                Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadData(profileImageUrl:String) {
        LoadingDialog.changeText(this,"Uploading User Data")
        val name=registerBinding.registerNameEditText.text.toString()
        val phone=registerBinding.registerPhoneEditText.text.toString()
        val email=registerBinding.registerEmailEditText.text.toString()

        val donorUser = User(name,phone,email,genderSelect,stateSelected,isDonor,bloodGroup,profileImageUrl)
        OfflineData(this).putUserBloodGroup(bloodGroup)
        OfflineData(this).putUserState(stateSelected)
        OfflineData(this).putUserIsDonor(isDonor)
        vBloodViewModel.updateUser(donorUser)
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data!!
            imageUri=uri
            registerBinding.registerProfilePic.setImageURI(imageUri)
        }
    }

    private fun resetValues() {
        registerBinding.registerNameEditText.text?.clear()
        registerBinding.registerPhoneEditText.text?.clear()
        registerBinding.registerEmailEditText.text?.clear()
        registerBinding.registerPasswordEditText.text?.clear()
        registerBinding.registerReEnterPasswordEditText.text?.clear()
        registerBinding.registerProfilePic.setImageResource(R.drawable.icon_person)
    }


    private fun checkInput(): Boolean {
        if(registerBinding.registerEmailEditText.text.isNullOrEmpty() && registerBinding.registerPasswordEditText.text.isNullOrEmpty()) return false
        return true
    }
}