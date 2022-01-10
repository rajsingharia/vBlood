package com.example.vblood.view.activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.databinding.ActivityLoginBinding
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var donorLoginBinding: ActivityLoginBinding

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        donorLoginBinding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(donorLoginBinding.root)


        donorLoginBinding.donorCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        donorLoginBinding.donorLoginButton.setOnClickListener {
            if(checkInput()){
                //TODO: Login
                LoadingDialog.showLoadingDialogBox(true,this)
                val email=donorLoginBinding.donorLoginEmailEditText.text.toString()
                val password=donorLoginBinding.donorLoginPasswordEditText.text.toString()
                vBloodViewModel.login(email,password)
                vBloodViewModel.currentUser.observe(this,{
                    it?.let {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                })

                vBloodViewModel.error.observe(this,{
                    it?.let{
                        Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                LoadingDialog.showLoadingDialogBox(false,this)
                Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkInput(): Boolean {
        if(donorLoginBinding.donorLoginEmailEditText.text.isNullOrEmpty() && donorLoginBinding.donorLoginPasswordEditText.text.isNullOrEmpty()) return false
        return true
    }

}