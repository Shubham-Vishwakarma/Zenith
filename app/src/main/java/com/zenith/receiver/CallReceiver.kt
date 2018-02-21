package com.zenith.receiver

import android.content.Context
import android.widget.Toast
import java.util.*


class CallReceiver : PhoneCallReceiver() {

    override fun onIncomingCallStarted(context: Context, number: String, start: Date){
        Toast.makeText(context,"Incoming call", Toast.LENGTH_SHORT).show()
    }

    override fun onIncomingCallEnded(context: Context, number: String, start: Date, end: Date){
        Toast.makeText(context,"Incoming ended",Toast.LENGTH_SHORT).show()
    }

    override fun onMissedCall(context: Context, number: String, start: Date){
        Toast.makeText(context,"Missed call",Toast.LENGTH_SHORT).show()
    }

}