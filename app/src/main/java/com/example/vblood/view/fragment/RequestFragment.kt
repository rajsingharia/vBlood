package com.example.vblood.view.fragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vblood.adapter.AllRequestAdapter
import com.example.vblood.application.vBloodApplication
import com.example.vblood.databinding.FragmentRequestBinding
import com.example.vblood.view.activity.AddRequestActivity
import com.example.vblood.viewmodel.vBloodViewModel
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory


class RequestFragment : Fragment() , AllRequestAdapter.onItemClickListener{

    private lateinit var requestBinding: FragmentRequestBinding

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((requireActivity().application as vBloodApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        requestBinding = FragmentRequestBinding.inflate(inflater,container,false)
        return requestBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter=AllRequestAdapter(this)
        adapter.initialize(vBloodViewModel,true)
        requestBinding.allRequestsRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        vBloodViewModel.myRequestData.observe(requireActivity(),{ allRequests->
            allRequests?.let{ data->
                adapter.submitList(data)
            }
        })

        requestBinding.allRequestsRecyclerView.adapter=adapter

        requestBinding.addRequestFloatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), AddRequestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(position: Int) {
        //TODO: DO NOTHING
    }

}