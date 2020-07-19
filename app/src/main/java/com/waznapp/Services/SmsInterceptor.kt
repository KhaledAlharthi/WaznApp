package com.waznapp.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.waznapp.Utilities.handleMessage


class SmsInterceptor : BroadcastReceiver() {

    private val TAG = javaClass.simpleName
    override fun onReceive(cntxt: Context, intent: Intent) {
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