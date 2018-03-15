package com.zenith.receiver

import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log


/**
 * Created by gurpreet on 1/3/18.
 */
class SmsReceiver : BroadcastReceiver() {

//    override fun onReceive(context: Context, intent: Intent) {
//        val intentExtras = intent.extras
//
//        if (intentExtras != null) {
//            /* Get Messages */
//            val sms = intentExtras.get("pdus") as Array<Any>
//
//            for (i in sms.indices) {
//                /* Parse Each Message */
//                val smsMessage = SmsMessage.createFromPdu(sms[i] as ByteArray)
//
//                val phone = smsMessage.getOriginatingAddress()
//                val message = smsMessage.getMessageBody().toString()
//
//                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    val sms = SmsManager.getDefault()

    override fun onReceive(context: Context, intent: Intent) {

        // Retrieves a map of extended data from the intent.
        val bundle = intent.extras

        try {

            if (bundle != null) {

                val pdusObj = bundle.get("pdus") as Array<Any>

                for (i in pdusObj.indices) {

                    val currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber = currentMessage.displayOriginatingAddress

                    val message = currentMessage.displayMessageBody

                    Log.i("SmsReceiver", "senderNum: $phoneNumber; message: $message")


                    // Show Alert
                    val duration = Toast.LENGTH_LONG
                    val toast = Toast.makeText(context,
                            "senderNum: $phoneNumber, message: $message", duration)
                    toast.show()

                } // end for loop
            } // bundle is null

        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e)

        }

    }
}