package com.example.vblood.model


data class Request(var id:String="",
                   var name:String="",
                   var from:String="",
                   var isPlasma:Boolean=false,
                   var bloodGroup:String="",
                   var time:String="",
                   var requestMessage:String ="",
                   var phoneNo:String="",
                   var location_x:Double=0.0,
                   var location_y:Double=0.0,
                   var isBooked:Boolean=false,
                   var requestExcept:String="na",
)
