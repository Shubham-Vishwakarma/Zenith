package com.zenith.activity

import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIConfiguration.RecognitionEngine
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zenith.R
import android.Manifest
import android.content.*
import android.provider.ContactsContract
import android.util.Log
import com.google.gson.JsonElement
import android.net.Uri
import android.widget.*

import android.speech.tts.TextToSpeech
import android.telephony.SmsMessage
import com.zenith.receiver.CallReceiver
import android.widget.Toast
import android.content.Intent




class MainActivity : AppCompatActivity(), AIListener{

//
//    private val CHECK_CODE = 0x1
//    private val LONG_DURATION = 5000
//    private val SHORT_DURATION = 1200
//
//    private var speaker: Speaker? = null
//
//    private var toggle: ToggleButton? = null
//    private var toggleListener: CompoundButton.OnCheckedChangeListener? = null
//
//    private val smsText: TextView? = null
//    private val smsSender: TextView? = null
//
//    private var smsReceiver: BroadcastReceiver? = null
//
//    private fun checkTTS() {
//        val check = Intent()
//        println("in checkTTS()")
//        check.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
//        startActivityForResult(check, CHECK_CODE)
//    }
//
//    protected override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
//        if (requestCode == CHECK_CODE)
//        {
//            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
//            {
//                speaker = Speaker(this)
//                println(speaker)
//            }
//            else
//            {
//                val install = Intent()
//                println(install)
//                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
//                startActivity(install)
//            }
//        }
//    }
//
//    private fun initializeSMSReceiver() {
//        smsReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//
//                val bundle = intent.extras
//                if (bundle != null) {
//                    val pdus = bundle.get("pdus") as Array<Any>
//                    for (i in pdus.indices) {
//                        val pdu = pdus[i] as ByteArray
//                        val message = SmsMessage.createFromPdu(pdu)
//                        val text = message.getDisplayMessageBody()
//                        val sender = getContactName(message.getOriginatingAddress())
//                       // val sender = getContactName()
//                        speaker?.pause(LONG_DURATION)
//                        speaker?.speak("You have a new message from$sender!")
//                        speaker?.pause(SHORT_DURATION)
//                        speaker?.speak(text)
//                        smsSender?.setText("Message from " + sender)
//                        smsText?.setText(text)
//                    }
//                }
//
//            }
//        }
//    }
//
//    private fun getContactName(phone:String):String {
//        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone))
//        val projection = arrayOf<String>(ContactsContract.Data.DISPLAY_NAME)
//        val cursor = getContentResolver().query(uri, projection, null, null, null)
//        if (cursor.moveToFirst())
//        {
//            return cursor.getString(0)
//        }
//        else
//        {
//            return "unknown number"
//        }
//    }
//
//    private fun registerSMSReceiver() {
//        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
//        registerReceiver(smsReceiver, intentFilter)
//    }


    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 1000
        var second : String?=null
        var first : String?=null
    }


    override fun onResult(result: AIResponse) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val res = result.getResult()
        val query = res.resolvedQuery.toString()
        println(query)
        val str= query.split("\\s+".toRegex())
        first=str[0]
        Log.e(first,"this is first part")
        second=str[1]
        Log.e(second,"this is second part")
        loadContacts()
    }
    private fun loadContacts() {
        var number : String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
            number = getContacts()
            if(number!="nothing") {
                if(first=="call") {
                    Log.e(second,"this is INSIDE if part")
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + number)
                    startActivity(callIntent)
                }
                else if(first=="message" || first=="send")
                {
                    Log.e(second,"this is INSIDE ELSE part")
                //    val uri = Uri.parse("smsto:" + number)
                    val smsIntent = Intent(Intent.ACTION_SENDTO)
                    smsIntent.data = Uri.parse("tel:" + number)
                    smsIntent.putExtra("sms_body", "SMS application launched from stackandroid.com example")
                    startActivity(smsIntent)
                }
                else if(first=="read")
                {
                   // val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
                    Log.e(second,"why here")
                }
                else
                {
                    Log.e(second,"last ")
                }
            }
        }
    }
    private fun getContacts(): String{
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null)
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                        if (second.equals(name,true)) {
                            var phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            if (phones.count > 0) {
                                while (phones.moveToNext()) {
                                    var phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    Toast.makeText(this, "Number " + phoneNumber, Toast.LENGTH_LONG).show()
                                    phones.close()
                                    cursor.close()
                                    return phoneNumber
                                }
                            }
                        }
                    }
                }
        Toast.makeText(this, "name not found ", Toast.LENGTH_LONG).show()
        cursor.close()
        return "nothing"
    }


    override fun onListeningStarted() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAudioLevel(level: Float) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: AIError?) {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningCanceled() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningFinished() {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        toggle = findViewById(R.id.speechToggle)
//       // smsText = findViewById(R.id.sms_text) as TextView
//      //  smsSender = findViewById(R.id.sms_sender) as TextView
//        toggleListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
//            if (isChecked) {
//                speaker?.allow(true)
//                speaker?.speak(getString(R.string.start_speaking))
//            } else {
//                speaker?.speak(getString(R.string.stop_speaking))
//                speaker?.allow(false)
//            }
//        }
//        //toggle.setOnCheckedChangeListener(toggleListener)
//        checkTTS()
//        initializeSMSReceiver()
//        registerSMSReceiver()

        val config = AIConfiguration("0c6d9a13bae340148af0528442089191",
                ai.api.AIConfiguration.SupportedLanguages.English,
                RecognitionEngine.System)

        val aiService = AIService.getService(this,config)
        aiService.setListener(this)
        var button = findViewById<Button>(R.id.start)
        button.setOnClickListener({
            aiService.startListening()

        })
    }
//
//    protected override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(smsReceiver)
//        speaker?.destroy()
//    }
}
