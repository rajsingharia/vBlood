package com.example.vblood.util

object Constants {

    var states = listOf(
        "Andhra Pradesh",
        "Arunachal Pradesh",
        "Assam",
        "Bihar",
        "Chandigarh",
        "Chhattisgarh",
        "Delhi",
        "Goa",
        "Gujarat",
        "Haryana",
        "Himachal Pradesh",
        "Jammu and Kashmir",
        "Kerala",
        "Madhya Pradesh",
        "Maharashtra",
        "Manipur",
        "Meghalaya",
        "Nagaland",
        "Orissa",
        "Punjab",
        "Rajasthan",
        "Sikkim",
        "Tamil Nadu",
        "Uttar Pradesh",
        "Uttarakhand",
        "West Bengal")

    var facts = listOf(
        "More than 4.5 million patients need blood transfusions each year in the U.S. and Canada.",
        " 43,000 pints: amount of donated blood used each day in the U.S. and Canada.",
        " Someone needs blood every two seconds.",
        " Only 37 percent of the U.S. population is eligible to donate blood – less than 10 percent do annually**.",
        " About 1 in 7 people entering a hospital need blood.",
        " One pint of blood can save up to three lives.",
        " Nearly 7% of the body weight of a human is made up of blood.",
        " 94 percent of blood donors are registered voters.",
        " Four main red blood cell types: A, B, AB and O. Each can be positive or negative for the Rh factor. AB is the universal recipient; O negative is the universal donor of red blood cells.",
        " Dr. Karl Landsteiner first identified the major human blood groups – A, B, AB and O – in 1901.",
        "More than 4.5 million patients need blood transfusions each year in the U.S. and Canada.",
        " 43,000 pints: amount of donated blood used each day in the U.S. and Canada.",
        " Someone needs blood every two seconds.",
        " Only 37 percent of the U.S. population is eligible to donate blood – less than 10 percent do annually**.",
        " About 1 in 7 people entering a hospital need blood.",
        " One pint of blood can save up to three lives.",
        " Nearly 7% of the body weight of a human is made up of blood.",
        " 94 percent of blood donors are registered voters.",
        " Four main red blood cell types: A, B, AB and O. Each can be positive or negative for the Rh factor. AB is the universal recipient; O negative is the universal donor of red blood cells.",
        " Dr. Karl Landsteiner first identified the major human blood groups – A, B, AB and O – in 1901."
    )

    const val state_s="SuccessFull"
    const val state_f="Failure"

    const val BASE_URL="https://fcm.googleapis.com"
    const val SERVER_KEY="AAAAUHipIzA:APA91bHjPX4s8MSW9SBUE477JjHzBmm1JdFYuwcKlU3z079YWwTm6unup4d9tsam_J_vxZgvu9609pHpYtearoEjAeny3xFyAvnYxFPdRm4ZM-sqb0QTc7O2FOMrCQHcD7RZ1aqDQ5oy"
    const val CONTENT_TYPE="application/json"
    const val topic="/topics/my_topic"

    fun getBloodGroupInWords(bg:String):String{
        when(bg){
            "A+" -> return "A_pe"
            "A-" -> return "A_ne"
            "B+" -> return "B_pe"
            "B-" -> return "B_ne"
            "O+" -> return "O_pe"
            "O-" -> return "O_ne"
            "AB+" -> return "AB_pe"
            "AB-" -> return "AB_ne"
        }
        return ""
    }

//    fun hasLocationPermission(context: Context){
//        EasyPermissions.hasPermissions(
//            context,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission
//        )
//    }

}