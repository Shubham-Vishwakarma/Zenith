package com.zenith.receiver

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import android.view.WindowManager
import kotlin.concurrent.thread
import android.media.AudioManager
import java.lang.Thread.sleep


class CallReceiver : PhoneCallReceiver() {

    companion object {
        @JvmField val TAG = "CallReceiver"
    }

    override fun onIncomingCallStarted(context: Context, number: String){
        Toast.makeText(context,"Incoming call", Toast.LENGTH_LONG).show()

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        try {
            try {
                Log.e(CallReceiver.TAG,"Picking up the call")
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK.toString())
                Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK.toString())

                thread(start = true){
                    try {
                        while (true) {
                            sleep(1000)
                            audioManager.mode = AudioManager.MODE_IN_CALL
                            if (!audioManager.isSpeakerphoneOn) {
                                Log.e(CallReceiver.TAG,"Turning on speaker")
                                audioManager.isSpeakerphoneOn = true
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
                            }
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            catch (ex:Exception){
                Log.e(CallReceiver.TAG,"Error = " + ex)
                val enforcedParam = "android.permission.CALL_PRIVILEGED"
                val btnDown = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT,KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK)
                )
                val btnUp = Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT,KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK)
                )

                context.sendOrderedBroadcast(btnDown,enforcedParam)
                context.sendOrderedBroadcast(btnUp,enforcedParam)
            }
        }
        catch (ex:Exception){
            Log.e(CallReceiver.TAG,"Error = " + ex)
        }

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