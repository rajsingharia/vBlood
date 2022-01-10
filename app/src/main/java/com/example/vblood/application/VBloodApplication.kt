package com.example.vblood.application

import android.app.Application
import com.example.vblood.firebase.FirebaseSource
import com.example.vblood.util.Repository

class vBloodApplication: Application() {

    private val firebaseSource by lazy {
        FirebaseSource()
    }

    val repository by lazy {
        Repository(firebaseSource)
    }

}