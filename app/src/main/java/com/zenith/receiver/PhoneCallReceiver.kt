package com.zenith.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

open class PhoneCallReceiver : BroadcastReceiver() {

    companion object {
        val TAG = "PhoneCallReceiver"
        var lastState : Int = TelephonyManager.CALL_STATE_IDLE
        var isIncoming : Boolean = false
        var savedNumber : String = ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals("android.intent.action.NEW_OUTGOING_CALL")){
            savedNumber = intent.extras.getString("android.intent.extra.PHONE_NUMBER")
        }
        else {
            if (intent.extras != null) {
                var stateStr = ""
                var number = ""
                val state: Int

                try {
                    stateStr = intent.extras.getString(TelephonyManager.EXTRA_STATE)
                    number = intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    Log.e(TAG,"Extras = " + intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER))
                }
                catch (ex: Exception){
                    Log.e(TAG,"Exception = " + ex)
                }

                state = if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    TelephonyManager.CALL_STATE_IDLE
                } else if (stateStr.equals(TelephonyManager.CALL_STATE_OFFHOOK)) {
                    TelephonyManager.CALL_STATE_OFFHOOK
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    TelephonyManager.CALL_STATE_RINGING
                } else {
                    0
                }

                Log.e(TAG, "State = " + state)

                onCallStateChanged(context, state, number)
            }
        }
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK
    // when it's answered, to IDLE when its hung up

    private fun onCallStateChanged(context: Context, state: Int, number: String){
        if(lastState == state){
            //No Change
            return
        }
        when(state){
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                savedNumber = number
                onIncomingCallStarted(context,number)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                //Transition of ringing->offhook are pickups of incoming call
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false
                    onOutgoingCallStarted(context, savedNumber)
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                //Went to idle - this is the end of a call
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup
                    onMissedCall(context, savedNumber)
                }
                else if (isIncoming){
                    onIncomingCallEnded(context, savedNumber)
                }
                else{
                    onOutgoingCallEnded(context, savedNumber)
                }
            }
        }
        lastState = state
    }

    protected open fun onIncomingCallStarted(context: Context, number: String){}
    protected open fun onOutgoingCallStarted(context: Context, number: String){}
    protected open fun onIncomingCallEnded(context: Context, number: String){}
    protected open fun onOutgoingCallEnded(context: Context, number: String){}
    protected open fun onMissedCall(context: Context, number: String){}
}