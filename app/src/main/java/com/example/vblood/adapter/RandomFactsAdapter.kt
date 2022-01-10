package com.example.vblood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vblood.R

class RandomFactsAdapter(var facts: List<String>): RecyclerView.Adapter<RandomFactsAdapter.factHolder>() {

    class factHolder (v: View) : RecyclerView.ViewHolder(v){
        var fact:TextView=v.findViewById(R.id.random_fact_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): factHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.random_fact_item_view,parent,false)
        return factHolder(view)
    }

    override fun onBindViewHolder(holder: factHolder, position: Int) {

        holder.fact.text = facts[position]
    }

    override fun getItemCount(): Int {
        return facts.size
    }

}