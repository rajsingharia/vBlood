package com.example.vblood.view.activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vblood.adapter.AllRequestAdapter
import com.example.vblood.application.vBloodApplication
import com.example.vblood.databinding.ActivityAllRequestListBinding
import com.example.vblood.model.Request
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.example.vblood.viewmodel.vBloodViewModel

class AllRequestListActivity : AppCompatActivity() , AllRequestAdapter.onItemClickListener{

    private lateinit var allRequestListBinding:ActivityAllRequestListBinding
    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((application as vBloodApplication).repository)
    }
    private var selectedRequest:MutableList<Request> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allRequestListBinding= ActivityAllRequestListBinding.inflate(layoutInflater)
        setContentView(allRequestListBinding.root)

        val adapter= AllRequestAdapter(this)
        adapter.initialize(vBloodViewModel,false)
        allRequestListBinding.allRequestsNotificationRecyclerView.layoutManager= LinearLayoutManager(this)

        vBloodViewModel.othersRequestData.observe(this,{ allRequests->
            selectedRequest.clear()
            for(element in allRequests){
                if(element.bloodGroup==OfflineData(this).getUserBloodGroup()) {
                    selectedRequest.add(element)
                }
            }
            adapter.submitList(selectedRequest)
        })

        allRequestListBinding.allRequestsNotificationRecyclerView.adapter=adapter

    }

    override fun onItemClick(position: Int) {
        val intent= Intent(this,MyRequestDetailActivity::class.java)
        intent.putExtra("id",selectedRequest[position].id)
        intent.putExtra("isBooked",selectedRequest[position].isBooked)
        startActivity(intent)
    }
}