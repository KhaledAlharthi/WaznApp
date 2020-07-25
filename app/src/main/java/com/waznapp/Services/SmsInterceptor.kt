package com.waznapp.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import com.waznapp.R
import com.waznapp.Utilities.handleMessage



/*
*  This class is responsible for receiving SMS messages and identifying them
*  if they belong to a Bank, then it'll extract the important information
*  and notifies user about they're newly made transaction.
*  We call this feature "Transaction Detection"
* */
class SmsInterceptor : BroadcastReceiver() {

    private val TAG = javaClass.simpleName
    override fun onReceive(cntxt: Context, intent: Intent) {
        /*
        *  First check if this feature is enabled by user
        *  if yes, then start the process.
        *  if no, abort and ignore
        * */
        /* Get user's save settings */
        val sharedPref = cntxt.getSharedPreferences(cntxt.getString(R.string.user_pref), Context.MODE_PRIVATE)
        /* Check the subscription state of Transaction Detection feature */
        val isSubscribed = sharedPref.getBoolean(cntxt.getString(R.string.transaction_detection_key), false)
        /* If state is False, abort & ignore */
        if (!isSubscribed) return

        /* Otherwise user has enabled the feature, start the process */

        /*
        *    Check if we're receiving a SMS message
        * */
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            /*
            *   Get extra data from intent
            * */
            val bundle = intent.extras
            /*
            *       Check if extras is not null
            * */
            if (bundle != null) {
                try {
                    /*
                    *   Extract SMS message data & format
                    * */
                    val pdus = bundle["pdus"] as Array<*>
                    val format = bundle["format"] as String;
                    val msgs = arrayOfNulls<SmsMessage>(pdus.size)
                    /*
                    *   Iterate over all indices to create a string of full message
                    * */
                    var message = ""
                    for (i in msgs.indices) {
                        /*
                        * Create SmsMessage object
                        * */
                        msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                        /*
                        * Extract message body only
                        * */
                        message += msgs[i]!!.messageBody
                    }
                    /*
                    *
                    *   Handle message, if it's from a bank
                    *        extract price and show a notification to user
                    * */

                    handleMessage(cntxt, message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}