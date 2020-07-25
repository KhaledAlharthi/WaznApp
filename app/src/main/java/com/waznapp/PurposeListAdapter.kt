package com.waznapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.waznapp.databinding.PurposeItemBinding


class PurposeListAdapter : RecyclerView.Adapter<PurposeListAdapter.ViewHolder>() {

    class ViewHolder(val binding: PurposeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem (model : PurposeModel){
            binding.dataModel = model

            binding.executePendingBindings()
        }
    }

    var data = mutableListOf<PurposeModel>()
    init {
        for (i in 0 .. 10) {
            val model = PurposeModel ("Title ${i+1}", R.drawable.ic_launcher_foreground)
            data.add (model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PurposeItemBinding.inflate(inflater, parent, false)
        return ViewHolder (binding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = data[position]
        holder.bindItem(dataModel)
    }


    class PurposeModel  (val title : String, val icon : Int)

}