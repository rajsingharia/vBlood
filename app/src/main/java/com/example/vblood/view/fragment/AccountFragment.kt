package com.example.vblood.view.fragment
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.vblood.R
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.LoadingDialog
import com.example.vblood.databinding.FragmentAccountBinding
import com.example.vblood.model.User
import com.example.vblood.util.Constants
import com.example.vblood.view.activity.DetailRequestActivity
import com.example.vblood.view.activity.MyRequestDetailActivity
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.github.drjacky.imagepicker.ImagePicker

class AccountFragment : Fragment() {

    private lateinit var accountBinding: FragmentAccountBinding
    private lateinit var user:User
    private var imageUri: Uri?=null

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((requireActivity().application as vBloodApplication).repository)
    }

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down_item, Constants.states)
        accountBinding.stateSelectDropDownMenu.setAdapter(adapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        accountBinding= FragmentAccountBinding.inflate(inflater,container,false)
        return accountBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBloodViewModel.userDetail.observe(requireActivity(),{
            it?.let{
                user=it
                loadDonorUserdata()
                accountBinding.accountSave.setOnClickListener {
                    LoadingDialog.showLoadingDialogBox(true,requireContext())
                    checkForImage()
                }
            }
        })

        accountBinding.accountProfilePic.setOnClickListener{
            ImagePicker.with(requireActivity())
                .cropSquare()
                .createIntentFromDialog { launcher.launch(it) }
        }


        accountBinding.accountRequestAccepted.setOnClickListener {
            vBloodViewModel.acceptedRequest.observe(requireActivity(),{
                if(it==null){
                    Toast.makeText(requireContext(),"Not Accepted Any Request Yet",Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent= Intent(requireContext(),MyRequestDetailActivity::class.java)
                    intent.putExtra("id",it.id)
                    intent.putExtra("isBooked",true)
                    startActivity(intent)
                }
            })
        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data!!
            imageUri=uri
            accountBinding.accountProfilePic.setImageURI(imageUri)
        }
    }

    private fun checkForImage() {
        val name=accountBinding.accountNameEditText.text.toString()
        val email=accountBinding.accountEmailEditText.text.toString()
        val phoneNo=accountBinding.accountPhoneEditText.text.toString()
        val state=accountBinding.stateSelectDropDownMenu.text.toString()
        val bloodGroup=accountBinding.accountBloodGroup.text.toString()
        if(imageUri==null){
            val updatedUser = User(name,phoneNo,email,user.gender,state,user.isDonor,bloodGroup,user.imageUrl,user.noOfRequestAccepted)
            upDateUser(updatedUser)
        }
        else{
            vBloodViewModel.uploadImage(imageUri)

            vBloodViewModel.uploadImageUrl.observe(requireActivity(),{
                it?.let{
                    Log.d("TEST","Image Uploaded")
                    val updatedUser = User(name,phoneNo,email,user.gender,state,accountBinding.donorSelectToggleGroup.isSelected,user.bloodGroup,it,user.noOfRequestAccepted)
                    upDateUser(updatedUser)
                }
            })
        }
    }

    private fun upDateUser(updatedUser: User) {
        vBloodViewModel.updateUser(updatedUser)
        vBloodViewModel.userState.observe(requireActivity(),{
            it?.let{
                LoadingDialog.showLoadingDialogBox(false,requireContext())
                if(it==Constants.state_s){
                    Toast.makeText(requireContext(),"Updated",Toast.LENGTH_SHORT).show()
                }
                else if(it==Constants.state_f){
                    Toast.makeText(requireContext(),"Unable to Update",Toast.LENGTH_SHORT).show()
                    loadDonorUserdata()
                }
            }
        })
    }

    private fun loadDonorUserdata() {
        accountBinding.accountNameEditText.setText(user.name)
        accountBinding.accountEmailEditText.setText(user.email)
        accountBinding.accountPhoneEditText.setText(user.phoneNo)
        accountBinding.stateSelectDropDownMenu.setText(user.state)
        accountBinding.accountBloodGroup.text = user.bloodGroup
        Glide
            .with(requireActivity())
            .load(user.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.icon_person)
            .into(accountBinding.accountProfilePic)

        if(user.isDonor) {
            accountBinding.donorSelectToggleGroup.selectButton(R.id.donor_select_ToggleButton_2)
        }
    }
}