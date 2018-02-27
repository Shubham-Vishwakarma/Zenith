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
import android.widget.Button
import android.widget.Toast
import com.zenith.R
import android.Manifest
import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.google.gson.JsonElement
import android.content.Intent
import android.net.Uri


class MainActivity : AppCompatActivity(), AIListener {


    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 1000
        var second : String?=null
    }


    override fun onResult(result: AIResponse) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val res = result.getResult()
        val query= res.resolvedQuery.toString()
        val str= query.split("\\s+".toRegex())
        val first=str[0]
        second=str[1]
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
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + number)
            startActivity(callIntent)
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
                var phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                if (phones.count > 0) {
                    while (phones.moveToNext()) {
                        var phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (second == name) {
                            Toast.makeText(this, "Number " + phoneNumber, Toast.LENGTH_LONG).show()
                            phones.close();
                            cursor.close()
                            return phoneNumber
                        } else {
                            //Toast.makeText(this, "name not found ", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        else {
            Toast.makeText(this, "name not found ", Toast.LENGTH_LONG).show()
        }
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
}
