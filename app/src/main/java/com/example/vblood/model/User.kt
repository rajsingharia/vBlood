package com.example.vblood.model

data class User(val name:String="",
                var phoneNo:String="",
                var email:String="",
                var gender:String="",
                var state:String="",
                var isDonor:Boolean=false,
                var bloodGroup:String="",
                var imageUrl:String="",
                var noOfRequestAccepted:Int=0
                )
