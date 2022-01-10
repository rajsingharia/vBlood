package com.example.vblood.util
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.vblood.firebase.FirebaseSource
import com.example.vblood.model.User
import com.example.vblood.model.PushNotification
import com.example.vblood.model.Request
import com.google.firebase.auth.FirebaseUser

class Repository(private val firebase: FirebaseSource) {

    fun logout() = firebase.logout()

    fun register(email:String,password:String)=firebase.register(email,password)

    fun login(email:String,password:String)=firebase.login(email,password)

    fun updateUser(user: User)=firebase.upDateUser(user)

    fun uploadImage(uri:Uri?) = firebase.upLoadImage(uri)

    fun verifyUserIsLoggedIn() = firebase.verifyUserIsLoggedIn()

    fun getUser() = firebase.getUser()

    fun requestForDonor(request: Request) = firebase.requestForDonor(request)

    fun getRequestDataForDonor(bloodGroup:String) = firebase.getRequestDataForDonor(bloodGroup)

    fun deleteRequestDataForDonor(id:String,bloodGroup:String) = firebase.deleteRequestDataForDonor(id, bloodGroup)

    fun bookRequestDataForDonor(id: String,bloodGroup:String) = firebase.bookRequestDataForDonor(id,bloodGroup)

    fun addNoOfRequests(uid:String) = firebase.addNoOfRequests(uid)

    suspend fun sendNotification(notification: PushNotification) = firebase.sendNotification(notification)


    private val currentUser = firebase.getCurrentUser()
    private val error = firebase.getError()
    private val userState = firebase.getUserState()
    private val uploadImageUrl = firebase.getUploadImageUrl()
    private val uploadImageUrlError = firebase.getUploadImageUrlError()
    private val user = firebase.getUserDetail()
    private val requestDonorError = firebase.getRequestDonorError()
    private val myRequestData=firebase.getMyRequestData()
    private val othersRequestData=firebase.getOthersRequestData()
    private val isBookedError=firebase.getIsBookedError()
    private val acceptedRequest=firebase.getAcceptedRequest()




    fun getCurrentUser(): MutableLiveData<FirebaseUser?> = currentUser
    fun getError(): MutableLiveData<String> = error
    fun getUserState() :MutableLiveData<String> = userState
    fun getUploadImageUrl():MutableLiveData<String?> = uploadImageUrl
    fun getUploadImageUrlError():MutableLiveData<String?> = uploadImageUrlError
    fun getUserDetail():MutableLiveData<User?> = user
    fun getRequestDonorError():MutableLiveData<String?> = requestDonorError
    fun getMyRequestData():MutableLiveData<MutableList<Request>> = myRequestData
    fun getOthersRequestData():MutableLiveData<MutableList<Request>> = othersRequestData
    fun getIsBookedError():MutableLiveData<String?> = isBookedError
    fun getAcceptedRequest():MutableLiveData<Request?> = acceptedRequest

}