package com.example.vblood.common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.vblood.databinding.DialogCustomLoadingBinding
import com.example.vblood.databinding.EligebilityCustomLoadingBinding

object eligebilityDialog {

    private lateinit var dialog:Dialog

    fun showLoadingDialogBox(context:Context) {
        dialog= Dialog(context)
        val binding: EligebilityCustomLoadingBinding = EligebilityCustomLoadingBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.show()
    }

}