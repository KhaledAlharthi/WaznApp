package com.waznapp.Utilities

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.view.forEachIndexed
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waznapp.database.TransactionViewModel

@BindingAdapter("showOnLoading")
fun View.showOnLoading (state : TransactionViewModel.TransactionState){
    visibility = when (state) {
        TransactionViewModel.TransactionState.Loading -> View.VISIBLE
        else -> View.GONE
    }
}


@BindingAdapter("hideOnLoading")
fun View.hideOnLoading (state : TransactionViewModel.TransactionState){
    visibility = when (state) {
        TransactionViewModel.TransactionState.Loading -> View.GONE
        else -> View.VISIBLE
    }
}


@BindingAdapter ("bind:newValue", "bind:newValueAttrChanged", requireAll = false)
fun Spinner.onItemSelection (value : String, changeListener : InverseBindingListener){
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeListener.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            changeListener.onChange()
        }
    }
    for (i in 0 until adapter.count){
        if (getItemAtPosition(i) == value){
            setSelection(i)
            break
        }
    }
}


@InverseBindingAdapter (attribute = "bind:newValue", event = "bind:newValueAttrChanged")
fun Spinner.getNewValue () : String {
    return selectedItem.toString()
}


@BindingAdapter ("bind:adapter")
fun RecyclerView.adapter (adapter : RecyclerView.Adapter<*>){
    layoutManager = LinearLayoutManager (context, LinearLayoutManager.HORIZONTAL, false)
    setAdapter(adapter)
}