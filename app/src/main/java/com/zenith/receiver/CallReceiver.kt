package com.zenith.receiver

import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.zenith.activity.MainActivity
import kotlin.concurrent.thread


class CallReceiver : PhoneCallReceiver(), AIListener {

    companion object {
        val TAG = "CallReceiver"
        lateinit var context: Context
        lateinit var progressDialog: ProgressDialog
    }

    override fun onIncomingCallStarted(context: Context, number: String){
        Toast.makeText(context,"Incoming call", Toast.LENGTH_LONG).show()

        CallReceiver.context = context

        thread(start = true){
            val config = ai.api.android.AIConfiguration(MainActivity.CLIENT_TOKEN,
                    AIConfiguration.SupportedLanguages.English,
                    ai.api.android.AIConfiguration.RecognitionEngine.System)

            val aiService = AIService.getService(CallReceiver.context,config)
            aiService.setListener(this)
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


    override fun onResult(result: AIResponse) {
        val res = result.result
        val query = res.resolvedQuery.toString()

        Log.e(TAG,"Query = " + query)
    }

    override fun onListeningStarted() {
        Log.e(TAG,"Listening Started")
        showProgressDialog()
    }

    override fun onAudioLevel(level: Float) {
    }

    override fun onError(error: AIError) {
        Log.e(TAG,"Error = " + error)
    }

    override fun onListeningCanceled() {
        Log.e(TAG,"Listening Cancelled")
    }

    override fun onListeningFinished() {
        Log.e(TAG,"Listening Finished")
        hideProgressDialog()
    }


    private fun someFunction(){
        /*val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        try {
            try {
                Log.e(TAG,"Picking up the call")
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
                Log.e(TAG,"Error = " + ex)
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
            Log.e(TAG,"Error = " + ex)
        }*/
    }

    private fun showProgressDialog(){
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Listening...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    private fun hideProgressDialog(){
        progressDialog.cancel()
    }

}