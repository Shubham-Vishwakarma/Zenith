package com.zenith.receiver

import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import java.util.*


/**
 * Created by gurpreet on 1/3/18.
 */
class SmsReceiver : BroadcastReceiver() {

    companion object {
        val TAG = "SmsReceiver"
        val
        lateinit var TTS:TextToSpeech
    }

    override fun onReceive(context: Context, intent: Intent) {

        // Retrieves a map of extended data from the intent.
        val bundle = intent.extras

        TTS = TextToSpeech(context,TextToSpeech.OnInitListener {
            Log.e(TAG,"Error Code = " + TextToSpeech.ERROR)
            TTS.language = Locale.US
        })

        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<*>

                for (i in pdusObj.indices) {
                    val currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val sender = getContactName(currentMessage.originatingAddress)
                    val phoneNumber = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody

                    Log.i(TAG, "senderNum: $phoneNumber; message: $message")
                    Toast.makeText(context, "senderNum: $phoneNumber, message: $message", Toast.LENGTH_LONG).show()
                }
            }
            else
                Log.e(TAG,"Bundle is null")

        } catch (e: Exception) {
            Log.e(TAG, "Exception smsReceiver" + e)
        }
    }
}