package com.zenith.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*


/**
 * Created by hanumaan on 3/4/18.
 */
class TextToSpeechService: Service(), TextToSpeech.OnInitListener {

    companion object {
        val TAG = "TextToSpeechService"
        val TEXT_TO_READ = "text"
        private val UTTERANCE_ID = "FINISHED_PLAYING"
        private lateinit var tts: TextToSpeech
        private lateinit var texts: ArrayList<String>
        private var isInit: Boolean = false
    }

    override fun onBind(p0: Intent): IBinder? {
        return null;
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(applicationContext,this)
        tts.setOnUtteranceProgressListener(utteranceProgressListener)
        Log.e(TAG,"OnCreate")
    }

    override fun onInit(status: Int) {
        Log.d(TAG, "onInit")
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.getDefault())
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak()
                isInit = true
            }
        }
    }

    private fun speak() {
        if (tts != null) {
            // Speak with 3 parameters deprecated but necessary on pre 21 version codes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // This is a single message
                var utteranceId: String? = null
                utteranceId = UTTERANCE_ID
                tts.speak(texts[0], TextToSpeech.QUEUE_FLUSH, null, utteranceId)

            } else {
                var myHashAlarm: HashMap<String, String>? = null
                myHashAlarm = HashMap()
                myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID)
                tts.speak(texts[0], TextToSpeech.QUEUE_FLUSH, myHashAlarm)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        texts = intent.getStringArrayListExtra(TextToSpeechService.TEXT_TO_READ)

        if (isInit) {
            speak()
        }

        return android.speech.tts.TextToSpeechService.START_NOT_STICKY
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    private val utteranceProgressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String) {
        }

        override fun onDone(utteranceId: String) {
            if (utteranceId == UTTERANCE_ID) {
                stopSelf()
            }
        }

        override fun onError(utteranceId: String) {
            stopSelf()
        }
    }

}