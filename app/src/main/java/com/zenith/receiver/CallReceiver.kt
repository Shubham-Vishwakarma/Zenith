package com.zenith.receiver

import android.content.Context
import android.widget.Toast


class CallReceiver : PhoneCallReceiver() {

    override fun onIncomingCallStarted(context: Context, number: String){
        Toast.makeText(context,"Incoming call", Toast.LENGTH_LONG).show()
    }

    override fun onOutgoingCallStarted(context: Context, number: String) {
        Toast.makeText(context,"Outgoing call", Toast.LENGTH_LONG).show()
    }

    override fun onIncomingCallEnded(context: Context, number: String){
        Toast.makeText(context,"Incoming ended",Toast.LENGTH_LONG).show()
    }

    override fun onOutgoingCallEnded(context: Context, number: String){
        Toast.makeText(context,"Outgoing ended",Toast.LENGTH_LONG).show()
    }

    override fun onMissedCall(context: Context, number: String){
        Toast.makeText(context,"Missed call",Toast.LENGTH_LONG).show()
    }

}