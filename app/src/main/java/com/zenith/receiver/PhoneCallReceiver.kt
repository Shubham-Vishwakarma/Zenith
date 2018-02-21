package com.zenith.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import java.util.*

open class PhoneCallReceiver : BroadcastReceiver() {

    companion object {
        @JvmField val TAG = "PhoneCallReceiver"
        var lastState : Int = TelephonyManager.CALL_STATE_IDLE
        var isIncoming : Boolean = false
        var callStartTime : Date? = null
        var savedNumber : String = ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        val stateStr : String = intent.extras.getString(TelephonyManager.EXTRA_STATE)
        val number : String = intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val state : Int

        state = if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            TelephonyManager.CALL_STATE_IDLE
        }
        else if (stateStr.equals(TelephonyManager.CALL_STATE_OFFHOOK)){
            TelephonyManager.CALL_STATE_OFFHOOK
        }
        else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            TelephonyManager.CALL_STATE_RINGING
        }
        else{
            0
        }

        Log.e(PhoneCallReceiver.TAG,"State = " + state)

        onCallStateChanged(context,state,number)
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
                callStartTime = Date()
                savedNumber = number
                onIncomingCallStarted(context,number, callStartTime!!)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                //Transition of ringing->offhook are pickups of incoming call
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false
                    callStartTime = Date()
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                //Went to idle - this is the end of a call
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup
                    onMissedCall(context, savedNumber, callStartTime!!)
                }
                else if (isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime!!,Date())
                }
                else{
                    onIncomingCallEnded(context, savedNumber, callStartTime!!,Date())
                }
            }
        }
        lastState = state
    }

    protected open fun onIncomingCallStarted(context: Context, number: String, start: Date){}
    protected open fun onIncomingCallEnded(context: Context, number: String, start: Date, end: Date){}
    protected open fun onMissedCall(context: Context, number: String, start: Date){}
}