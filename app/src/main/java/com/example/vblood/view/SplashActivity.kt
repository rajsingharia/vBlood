package com.example.vblood.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.example.vblood.application.vBloodApplication
import com.example.vblood.databinding.ActivitySplashBinding
import com.example.vblood.util.OfflineData
import com.example.vblood.view.activity.HomeActivity
import com.example.vblood.view.activity.LoginActivity
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        vBloodViewModel.verifyUserIsLoggedIn()

        Handler().postDelayed({
            checkForUser()
        }, 4000)

    }

    private fun checkForUser() {
        val userType=OfflineData(this).getUserType()
        vBloodViewModel.currentUser.observe(this,{
            if(it!=null){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}