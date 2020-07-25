package com.waznapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.waznapp.Utilities.*
import com.waznapp.database.TransactionViewModel
import com.waznapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val SMS_PERMISSION_REQUEST_CODE = 101

    private val transactionDetectionSubscription = MutableLiveData<Boolean>().apply { value = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        transactionDetectionSubscription.value = getTransactionDetectionSubscriptionSetting()

        val transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.transactionVM = transactionViewModel;
        binding.eventHandler = EventHandler()
        binding.isSubscribedToTransactionDetection = transactionDetectionSubscription
        binding.lifecycleOwner = this

        /*
        *   Check if MainActivity was launched by notification,
        *       if yes, then show a dialog to handle the action.
        * */
        if (intent != null && intent.hasExtra(getString(R.string.notification_data_intent_key))){
            val notificationData = intent.getBundleExtra(getString(R.string.notification_data_intent_key))
            notificationData?.let { handleNotification(it) }
        }


        testNotification()
    }

    /**
     *  This function will be called when user click on notification and the app is open
     * */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        /*
        *   Check if MainActivity was launched by notification,
        *       if yes, then show a dialog to handle the action.
        * */
        if (intent != null && intent.hasExtra(getString(R.string.notification_data_intent_key))){
            val notificationData = intent.getBundleExtra(getString(R.string.notification_data_intent_key))
            notificationData?.let { handleNotification(it) }
        }
    }


    /**
     *  Display a test notification
     * */
    private fun testNotification (){
        handleMessage(this, CASE_1_MESSAGE)
    }


    /**
     *  Handles different notification types.
     *  @param intent Intent extra
     * */
    private fun handleNotification (notificationData : Bundle){
        /*
        *   Extract type of notification from extras
        * */
        val notificationType = notificationData.getString(getString(R.string.notification_type_intent_key), null)
        when (notificationType){
            /*
            *  If it's a new transaction, get extra information
            *   and show a dialog to handle inserting this transaction
            **/
            TRANSACTION_NOTIFICATION_TYPE -> {
                // Show a dialog to handle transaction
                showNewTransactionDialog(notificationData)
            }
        }
    }

    /**
     *  Displays a dialog to handle the transaction
     *  @param transaction details of transaction detected
     * */
    private fun showNewTransactionDialog (notificationData : Bundle){
        val dialog = NewTransactionDialog ()
        // Pass extras to dialog so it can extract transaction details
        dialog.arguments = notificationData
        dialog.show(supportFragmentManager, "NEW_TRANSACTION")
    }


    /*
    *  Returns the subscription state of Transaction Detection feature
    * */
    private fun getTransactionDetectionSubscriptionSetting () : Boolean {
        val sharedPref = getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
        return sharedPref.getBoolean(getString(R.string.transaction_detection_key), false)
    }

    /**
     *  Changes the subscription state of Transaction Detection feature
     *  @param isSubscribed the current state of subscription
    * */
    private fun transactionDetectionToggleSate (isSubscribed : Boolean) {
        val sharedPref = getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
        val newValue = !isSubscribed
        sharedPref.edit().putBoolean(getString(R.string.transaction_detection_key), newValue).apply()
        transactionDetectionSubscription.value = newValue
    }

    inner class EventHandler {


        /*
        *   Changes the subscription state of Transaction Detection feature
        * */
        fun transactionDetectionSubscribeButtonClicked(){
            /*
            *   The feature requires the permission to receive SMS message
            *   Check if the permission is granted
            *   otherwise, request permission from user
            * */
            if (isSmsPermissionGranted(this@MainActivity)) {
                // Permission is granted, change subscription state
                transactionDetectionSubscription.value?.let {transactionDetectionToggleSate(it)}
            } else {
                // Permission is not granted
                requestSmsPermissions(this@MainActivity, SMS_PERMISSION_REQUEST_CODE)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            SMS_PERMISSION_REQUEST_CODE -> {
                val granted = !(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                if (granted) {
                    /*
                    *   Change the state of subscription when user grants permission
                    * */
                    transactionDetectionSubscription.value?.let { transactionDetectionToggleSate(it) }
                } else {
                    // If permission is not granted, show an error message
                    Toast.makeText(this@MainActivity, getString(R.string.sms_required_error_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}