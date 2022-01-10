package com.example.vblood.viewmodel
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vblood.model.User
import com.example.vblood.model.PushNotification
import com.example.vblood.model.Request
import com.example.vblood.util.Repository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class vBloodViewModel(private val repository: Repository) :ViewModel(){

        fun logout() = viewModelScope.launch {
            repository.logout()
        }

        fun register(email: String, password: String) = viewModelScope.launch {
            repository.register(email, password)
        }

        fun login(email: String, password: String) = viewModelScope.launch {
            repository.login(email, password)
        }

        fun updateUser(user: User) = viewModelScope.launch {
            repository.updateUser(user)
        }

        fun uploadImage(uri: Uri?) = viewModelScope.launch {
            repository.uploadImage(uri)
        }

        fun verifyUserIsLoggedIn() = viewModelScope.launch {
            repository.verifyUserIsLoggedIn()
        }

        fun getUser() = viewModelScope.launch {
            repository.getUser()
        }

        fun requestForDonor(request:Request) = viewModelScope.launch {
            repository.requestForDonor(request)
        }

        fun getRequestDataForDonor(bloodGroup:String) = viewModelScope.launch {
            repository.getRequestDataForDonor(bloodGroup)
        }

        fun deleteRequestDataForDonor(id:String,bloodGroup:String) = viewModelScope.launch {
            repository.deleteRequestDataForDonor(id,bloodGroup)
        }

        fun bookRequestDataForDonor(id: String,bloodGroup:String) = viewModelScope.launch {
            repository.bookRequestDataForDonor(id,bloodGroup)
        }

        fun sendNotification(notification: PushNotification) = viewModelScope.launch {
            repository.sendNotification(notification)
        }

        fun addNoOfRequest(uid:String) = viewModelScope.launch {
            repository.addNoOfRequests(uid)
        }


        var currentUser: MutableLiveData<FirebaseUser?> = repository.getCurrentUser()

        var error: MutableLiveData<String> = repository.getError()

        var userState:MutableLiveData<String> = repository.getUserState()

        var uploadImageUrl:MutableLiveData<String?> = repository.getUploadImageUrl()

        var uploadImageUrlError:MutableLiveData<String?> = repository.getUploadImageUrlError()

        var userDetail:MutableLiveData<User?> = repository.getUserDetail()

        var requestDonorError:MutableLiveData<String?> = repository.getRequestDonorError()

        var myRequestData:MutableLiveData<MutableList<Request>> = repository.getMyRequestData()

        var othersRequestData:MutableLiveData<MutableList<Request>> = repository.getOthersRequestData()

        var isBookedError:MutableLiveData<String?> = repository.getIsBookedError()

        var acceptedRequest:MutableLiveData<Request?> = repository.getAcceptedRequest()

    }


    class LoginRegistrationViewModelFactory(private val repository: Repository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(vBloodViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return vBloodViewModel(repository) as T
            }
            throw IllegalArgumentException("Wrong View Model")
        }

    }

