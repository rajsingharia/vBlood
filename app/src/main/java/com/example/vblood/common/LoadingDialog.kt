package com.example.vblood.common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.vblood.databinding.DialogCustomLoadingBinding

object LoadingDialog {

    private lateinit var dialog:Dialog

    fun showLoadingDialogBox(visible:Boolean,context:Context) {
        if(!visible) {
            dialog.dismiss()
            return
        }
        dialog= Dialog(context)
        val binding: DialogCustomLoadingBinding = DialogCustomLoadingBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun changeText(context:Context,text:String){
        val binding: DialogCustomLoadingBinding = DialogCustomLoadingBinding.inflate(LayoutInflater.from(context))
        binding.loadingTextView.text=text
        dialog.setContentView(binding.root)
        if(dialog.isShowing){
            dialog.dismiss()
            dialog.show()
        }
    }

}