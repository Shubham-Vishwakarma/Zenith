package com.zenith

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import java.util.*
import android.media.AudioManager
import android.system.Os.shutdown
import android.util.Log
import com.zenith.receiver.CallReceiver


/**
 * Created by gurpreet on 28/2/18.
 */
class Speaker(context: Context) : OnInitListener {
    private val TAG = "Speaker"
    private var tts: TextToSpeech = TextToSpeech(context, this)

    private var ready = false

    private var allowed = false

    init {
        Log.e(TextToSpeech.SUCCESS.toString(),"TTS success")
    }

    fun allow(allowed: Boolean) {
        this.allowed = allowed
    }

    fun isAllowed(): Boolean {
        return allowed;
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            Log.e(TAG,"TTS success = " + TextToSpeech.SUCCESS)
            tts.language = Locale.US;
            ready = true;
        }else{
            ready = false;
        }

    }

    fun speak(text: String) {
        if (ready && allowed) {
            val hash = HashMap<String, String>()
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    AudioManager.STREAM_NOTIFICATION.toString())
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash)
        }
    }

    fun pause(duration: Int) {
        tts.playSilence(duration.toLong(), TextToSpeech.QUEUE_ADD, null)
    }

    // Free up resources
    fun destroy() {
        tts.shutdown()
    }


}
