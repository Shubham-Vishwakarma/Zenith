//package com.zenith
//
//import android.content.Context
//import android.speech.tts.TextToSpeech
//import android.speech.tts.TextToSpeech.OnInitListener
//import java.util.*
//import android.media.AudioManager
//import android.system.Os.shutdown
//import android.util.Log
//import com.zenith.receiver.CallReceiver
//
//
///**
// * Created by gurpreet on 28/2/18.
// */
//class Speaker(context: Context) : OnInitListener {
//    private var tts: TextToSpeech? = null
//
//    private var ready = false
//
//    private var allowed = false
//
//    init {
//        tts = TextToSpeech(context, this)
//        Log.e(TextToSpeech.SUCCESS.toString(),"TTS success")
//    }
//
//    fun allow(allowed: Boolean) {
//        this.allowed = allowed
//    }
//
//    override fun onInit(status: Int) {
//        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        if(status == TextToSpeech.SUCCESS){
//            // Change this to match your
//            // locale
//            Log.e(TextToSpeech.SUCCESS.toString(),"TTS success")
//            tts?.setLanguage(Locale.US);
//            ready = true;
//        }else{
//            ready = false;
//        }
//
//    }
//
//    fun speak(text: String) {
//
//        // Speak only if the TTS is ready
//        // and the user has allowed speech
//
//        if (ready && allowed) {
//            val hash = HashMap<String, String>()
//            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
//                    AudioManager.STREAM_NOTIFICATION.toString())
//            tts?.speak(text, TextToSpeech.QUEUE_ADD, hash)
//        }
//    }
//
//    fun pause(duration: Int) {
//        tts?.playSilence(duration.toLong(), TextToSpeech.QUEUE_ADD, null)
//    }
//
//    // Free up resources
//    fun destroy() {
//        tts?.shutdown()
//    }
//
//
//}
