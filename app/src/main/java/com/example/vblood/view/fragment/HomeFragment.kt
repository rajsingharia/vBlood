package com.example.vblood.view.fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vblood.adapter.RandomFactsAdapter
import com.example.vblood.application.vBloodApplication
import com.example.vblood.common.eligebilityDialog
import com.example.vblood.databinding.FragmentHomeBinding
import com.example.vblood.model.User
import com.example.vblood.util.Constants
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.LoginRegistrationViewModelFactory
import com.example.vblood.viewmodel.vBloodViewModel
import com.google.android.gms.tasks.Task
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate


class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var user: User

    private val vBloodViewModel: vBloodViewModel by viewModels{
        LoginRegistrationViewModelFactory((requireActivity().application as vBloodApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding= FragmentHomeBinding.inflate(inflater,container,false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBloodViewModel.userDetail.observe(requireActivity(), {
            it?.let {
                user=it
                homeBinding.homeFragmentName.text = user.name.uppercase()
                homeBinding.fragmentHomePoints.text=user.noOfRequestAccepted.toString()
                OfflineData(requireActivity()).putUserIsDonor(user.isDonor)
            }
        })

        val facts=getRandomFacts()
        val adapter = RandomFactsAdapter(facts)

        val layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        homeBinding.randomFactsRecyclerView.layoutManager = layoutManager

        homeBinding.fragmentHomeEligibility.setOnClickListener {
            eligebilityDialog.showLoadingDialogBox(requireContext())
        }


        homeBinding.randomFactsRecyclerView.adapter=adapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(homeBinding.randomFactsRecyclerView)


        homeBinding.fragmentHomeShare.setOnClickListener {
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            val shareMessage = "vBlood\nJoin this blood donation application\nhttps://drive.google.com/file/d/1LaKSjwft2vzpQnlHsFaerk8B-YWCyW1R/view?usp=sharing"
            whatsappIntent.putExtra(
                Intent.EXTRA_TEXT,
                shareMessage
            )
            startActivity(whatsappIntent)
        }
    }


    private fun getRandomFacts(): List<String> {
        val rnds = (0..10).random()
        val f=Constants.facts.subList(rnds,rnds+5)
        return f

    }

}