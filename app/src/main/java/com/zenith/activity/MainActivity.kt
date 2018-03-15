package com.zenith.activity

import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIConfiguration.RecognitionEngine
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.zenith.R


class MainActivity : AppCompatActivity(), AIListener{

    companion object {
        val TAG = "MainActivity"
        val CLIENT_TOKEN = "0c6d9a13bae340148af0528442089191"
        val PERMISSIONS_REQUEST_READ_CONTACTS = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = AIConfiguration(CLIENT_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                RecognitionEngine.System)

        val aiService = AIService.getService(this,config)
        aiService.setListener(this)
        val button = findViewById<Button>(R.id.start)
        button.setOnClickListener({
            aiService.startListening()
        })
    }


    override fun onResult(result: AIResponse) {
        val res = result.result
        val query = res.resolvedQuery.toString()

        val str= query.split("\\s+".toRegex())

        Log.e(TAG,"First = " + str[0])
        Log.e(TAG,"Second = " + str[1])
        loadContacts(str[0],str[1])
    }

    override fun onListeningStarted() {
        Log.e(TAG,"Listening Started")
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
    }

    private fun loadContacts(func: String,name: String) {
        val number : String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        else {
            number = getContacts(name)
            Log.e(TAG,"Number = " + number)
            if(number != "nothing") {
                if(func == "call") {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + number)
                    startActivity(callIntent)
                }
                else if(func == "message" || func == "send")
                {
                    val uri: Uri = Uri.parse("smsto:" + number)
                    val smsIntent = Intent(Intent.ACTION_SENDTO,uri)
                    smsIntent.putExtra("sms_body", "SMS application launched from Zenith")
                    startActivity(smsIntent)
                }
            }
        }
    }

    private fun getContacts(name: String): String{
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

            if (name.equals(displayName,true)) {
                val phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)

                while (phones.moveToNext()) {
                    val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Toast.makeText(this, "Number " + phoneNumber, Toast.LENGTH_LONG).show()
                    phones.close()
                    cursor.close()
                    return phoneNumber
                }
            }
        }


        Toast.makeText(this, "name not found ", Toast.LENGTH_LONG).show()
        cursor.close()
        return "nothing"
    }
}
