package com.example.vblood.firebase
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vblood.model.User
import com.example.vblood.model.PushNotification
import com.example.vblood.model.Request
import com.example.vblood.retrofit.RetrofitInstance
import com.example.vblood.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

class FirebaseSource {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fireStore : FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }

    private val fireUpload by lazy {
        Firebase.storage.reference
    }


    private val fireRealtimeData by lazy {
        Firebase.database
    }


    //mutable variables
    private var currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData<FirebaseUser?>()
    private var error: MutableLiveData<String> = MutableLiveData<String>()
    private var userState:MutableLiveData<String> = MutableLiveData<String>()
    private var userDetails:MutableLiveData<User?> = MutableLiveData<User?>()
    private var uploadImageError:MutableLiveData<String?> = MutableLiveData<String?>()
    private var uploadImageUrl:MutableLiveData<String?> = MutableLiveData<String?>()
    private var requestDonorError:MutableLiveData<String?> = MutableLiveData<String?>()
    private var isBookedError:MutableLiveData<String?> = MutableLiveData<String?>()
    private var myRequestData:MutableLiveData<MutableList<Request>> = MutableLiveData<MutableList<Request>>()
    private var othersRequestData:MutableLiveData<MutableList<Request>> = MutableLiveData<MutableList<Request>>()
    private var acceptedRequest:MutableLiveData<Request?> = MutableLiveData()


    fun logout(){
        firebaseAuth.signOut()
    }

    fun verifyUserIsLoggedIn(){
        val user:FirebaseUser?=firebaseAuth.currentUser
        currentUser.postValue(user)
    }


    fun register(email: String, password: String){

        currentUser.value=null

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                currentUser.postValue(it.result?.user)
            }
            else{
                error.postValue(it.exception?.message.toString())
            }
        }
    }


    fun login(email: String, password: String){

        currentUser.value=null

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                currentUser.postValue(it.result?.user)
            }
            else{
                error.postValue(it.exception?.message.toString())
            }
        }
    }

    fun upDateUser(user: User){
        fireStore
            .collection("User")
            .document(currentUser.value?.uid.toString())
            .set(user)
            .addOnSuccessListener {
                userDetails.postValue(user)
                userState.postValue(Constants.state_s)
            }
    }



    fun getUser() {
        fireStore
            .collection("User")
            .document(currentUser.value?.uid.toString())
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val user= it.result!!.toObject(User::class.java)
                    user?.let {
                        userDetails.postValue(user)
                        userState.postValue(Constants.state_s)
                    }
                }
                else{
                    userState.postValue(Constants.state_f)
                    userDetails.postValue(null)
                }
            }
    }

    fun upLoadImage(file: Uri?){
        file?.let{ f->
                val uid=currentUser.value?.uid.toString()
                val ref=fireUpload.child("profile_images/$uid.jpg")

                ref.putFile(f)
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            uploadImageError.postValue(Constants.state_s)
                            ref.downloadUrl.addOnSuccessListener {imageUrl->
                                imageUrl?.let {
                                    uploadImageUrl.postValue(imageUrl.toString())
                                }
                            }

                        }
                        else{
                            uploadImageError.postValue(Constants.state_f)
                        }
                    }
            }
    }

    fun requestForDonor(request: Request){
        requestDonorError.value=null
        val uid=firebaseAuth.currentUser!!.uid
        request.from=uid
        val reference=fireRealtimeData.getReference("Request")
        val ukey=reference.push().key.toString()
        request.id=ukey
        reference.child(request.bloodGroup).child(ukey).setValue(request)
            .addOnSuccessListener { requestDonorError.postValue(Constants.state_s) }
            .addOnFailureListener { requestDonorError.postValue(it.message.toString()) }
            .addOnCanceledListener { requestDonorError.postValue(Constants.state_f) }
    }



    fun getRequestDataForDonor(bloodGroup:String){
        val uid=firebaseAuth.currentUser!!.uid
        fireRealtimeData.getReference("Request").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                requestDonorError.value=null

                val myDataList:MutableList<Request> = mutableListOf()
                val othersDataList:MutableList<Request> = mutableListOf()
                var dataFound:Request?=null

                for(data in snapshot.children){
                    for(subChildren in data.children){
                        val singleData=subChildren.getValue(Request::class.java)
                        singleData?.let {
                            if(singleData.requestExcept==uid){
                                dataFound=singleData
                            }
                            if(singleData.from==uid) {
                                myDataList.add(it)
                            }
                            else {
                                othersDataList.add(it)
                            }
                        }
                    }
                }
                acceptedRequest.postValue(dataFound)
                myRequestData.postValue(myDataList)
                othersRequestData.postValue(othersDataList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("raj","Firebase : $error")
            }
        })
    }




    fun deleteRequestDataForDonor(id:String,bloodGroup: String){
        val uid=firebaseAuth.currentUser!!.uid
        fireRealtimeData.getReference("Request/${bloodGroup}").child(id).removeValue()
    }



    fun bookRequestDataForDonor(id:String,bloodGroup: String){
        val uid=firebaseAuth.currentUser!!.uid
        val reference=fireRealtimeData.getReference("Request")

        reference.child(bloodGroup).child(id).child("booked").setValue(true)
            .addOnSuccessListener { isBookedError.postValue(Constants.state_s) }
            .addOnFailureListener { isBookedError.postValue(it.message.toString()) }
            .addOnCanceledListener { isBookedError.postValue(Constants.state_f) }

        reference.child(bloodGroup).child(id).child("requestExcept").setValue(uid)
            .addOnSuccessListener { isBookedError.postValue(Constants.state_s) }
            .addOnFailureListener { isBookedError.postValue(it.message.toString()) }
            .addOnCanceledListener { isBookedError.postValue(Constants.state_f) }

    }



    suspend fun sendNotification(notification: PushNotification){
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d("raj","Response: ${Gson().toJson(response)}")
            }
            else{
                Log.d("raj",response.errorBody().toString())
            }
        } catch (e:Exception){
            Log.d("raj",e.toString())
        }
    }



    fun addNoOfRequests(uid:String){

        fireStore
            .collection("User")
            .document(uid)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val user= it.result!!.toObject(User::class.java)
                    user?.let { upDatedUser->
                        upDatedUser.noOfRequestAccepted= upDatedUser.noOfRequestAccepted+1
                        //Log.d("raj","no of request accepted now ${upDatedUser.noOfRequestAccepted}")
                        fireStore
                            .collection("User")
                            .document(uid)
                            .set(upDatedUser)
                    }
                }
            }
    }


    fun getCurrentUser(): MutableLiveData<FirebaseUser?> = currentUser
    fun getError(): MutableLiveData<String> = error
    fun getUserState(): MutableLiveData<String> = userState
    fun getUploadImageUrl():MutableLiveData<String?> = uploadImageUrl
    fun getUploadImageUrlError():MutableLiveData<String?> = uploadImageError
    fun getUserDetail():MutableLiveData<User?> = userDetails
    fun getRequestDonorError():MutableLiveData<String?> = requestDonorError
    fun getMyRequestData():MutableLiveData<MutableList<Request>> = myRequestData
    fun getOthersRequestData():MutableLiveData<MutableList<Request>> = othersRequestData
    fun getIsBookedError():MutableLiveData<String?> = isBookedError
    fun getAcceptedRequest():MutableLiveData<Request?> = acceptedRequest


}