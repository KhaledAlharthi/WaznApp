package com.waznapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.waznapp.Utilities.showNotification
import com.waznapp.database.Transaction
import com.waznapp.database.TransactionViewModel
import com.waznapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        val transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        binding.transaction = transactionViewModel;
        binding.lifecycleOwner = this


        val t = Transaction(0, 495.00, "", "", "", "", "", "", 0)
        showNotification(this, getString(R.string.purchase_detected_notification_no_amount), t)
    }
}