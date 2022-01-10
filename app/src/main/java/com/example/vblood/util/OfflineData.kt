package com.example.vblood.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME="my_preference"

class OfflineData(activity: Activity) {

    private val sharedPreferences: SharedPreferences = activity.getSharedPreferences( PREFERENCE_NAME,
        Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor =  sharedPreferences.edit()

    fun putUserType(x:String?){
        editor.putString("type",x)
        editor.apply()
        editor.commit()
    }

    val type=sharedPreferences.getString("type",null)
    val isDonor=sharedPreferences.getBoolean("isDonor",false)
    private val userInfo:Boolean=sharedPreferences.getBoolean("userInfo",false)
    private val bloodGroup:String?=sharedPreferences.getString("blood",null)
    private val state:String?=sharedPreferences.getString("state",null)

    fun getUserType():String?{
        return type
    }

    fun putUserIsDonor(x:Boolean){
        editor.putBoolean("isDonor",x)
        editor.apply()
        editor.commit()
    }

    fun getUserIsDonor():Boolean{
        return isDonor
    }

    fun putUserBloodGroup(b:String?){
        editor.putString("blood",b)
        editor.apply()
        editor.commit()
    }

    fun getUserBloodGroup():String?{
        return bloodGroup
    }

    fun putUserState(s:String?){
        editor.putString("state",s)
        editor.apply()
        editor.commit()
    }

    fun getUserState():String?{
        return state
    }

}