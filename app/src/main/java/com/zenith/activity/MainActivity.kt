package com.zenith.activity

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIConfiguration.RecognitionEngine
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.zenith.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), AIListener{

    companion object {
        val TAG = "MainActivity"
        val REQ_CODE_SPEECH_INPUT = 100
        val CLIENT_TOKEN = "0c6d9a13bae340148af0528442089191"
        val PERMISSIONS_REQUEST_READ_CONTACTS = 1000
        lateinit var progressDialog: ProgressDialog
        var number = "0000000000"
        val app_name_list = ArrayList<String>()
        val app_package_list = ArrayList<String>()
        val music_name_list = ArrayList<String>()
        val music_total_list = ArrayList<String>()
        private val STAR = arrayOf("*")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        findPackageName()
        ListAllSongs()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = AIConfiguration(CLIENT_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                RecognitionEngine.System)

        val aiService = AIService.getService(this,config)
        aiService.setListener(this)
        val button = findViewById<ImageButton>(R.id.start)
        button.setOnClickListener{
            aiService.startListening()
        }

    }


    /**
     * Showing google speech input dialog
     * */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        }
        catch (a: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Receiving speech input
     * */
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    sendMessageIntent(result[0])
                }
            }
        }
    }

    fun findPackageName(){

        var intent= Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        //var packages = pm.queryIntentActivities(intent, 0)
        var packageAppsList =this.getPackageManager().queryIntentActivities(intent, 0)
        val pm = getPackageManager()


        for (reso in packageAppsList ){

            try
            {
                val package_name = reso.activityInfo.packageName
                Log.e(TAG, reso.toString())
                val app_name = pm.getApplicationLabel(
                        pm.getApplicationInfo(package_name, PackageManager.GET_META_DATA)) as String
                var same = false
                for (i in 0 until app_name_list.size)
                {
                    if (package_name == app_package_list.get(i))
                        same = true
                }
                if (!same)
                {
                    app_name_list.add(app_name)
                    Log.e(TAG, app_name.toString())

                    app_package_list.add(package_name)
                }

            }
            catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(this,"No such APP",Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onResult(result: AIResponse) {
        val res = result.result
        val query = res.resolvedQuery.toString()

       // val str= query.split("\\s+".toRegex())

        Log.e(TAG,"Action = " + query.substringBefore(" "))
        Log.e(TAG,"Parameter = " + query.substringAfter(" "))

        performAction(query.substringBefore(" "),query.substringAfter(" "))
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

    @SuppressLint("MissingPermission")
    private fun performAction(action: String, param: String){
        when (action){
            "call" -> {
                number = loadContacts(param)

                if(number.isNotEmpty()) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:" + number)
                    startActivity(callIntent)
                }
            }
            "message" -> {
                number = loadContacts(param)
                if(number.isNotEmpty()) {
                    promptSpeechInput()
                    Toast.makeText(this, " $param: $number", Toast.LENGTH_SHORT).show()
                }
            }
            "open" -> {

                var intent= Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                for (i in 0 until app_name_list.size){

                    try
                    {
                           // Log.e(TAG, app_name_list.get(i).toString())
                            if (app_name_list.get(i).compareTo(param, true)==0)
                            {
                                Toast.makeText(this,"This works!",Toast.LENGTH_LONG).show()
                                val launchIntent = getPackageManager().getLaunchIntentForPackage(app_package_list.get(i))
                                if (launchIntent != null)
                                {
                                    startActivity(launchIntent)//null pointer check in case package name was not found
                                }
                            }

                    }
                    catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(this,"No such APP",Toast.LENGTH_LONG).show()
                    }

                }
                }
            "play" -> {
                    playSearchArtist(param)
            }

            else -> {
                Toast.makeText(this,"Action cannot be performed",Toast.LENGTH_LONG).show()
            }
        }
    }




    fun ListAllSongs() {
        val cursor: Cursor?
        val allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        if (isSdPresent()) {
            cursor = contentResolver.query(allsongsuri, STAR, selection, null, null)

            if (cursor != null) {
                if (cursor!!.moveToFirst()) {
                    do {
                        val songname = cursor!!
                                .getString(cursor!!
                                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                        Log.e(TAG,"song name = " + songname)
                       /* val song_id = cursor!!.getInt(cursor!!
                                .getColumnIndex(MediaStore.Audio.Media._ID))

                        val fullpath = cursor!!.getString(cursor!!
                                .getColumnIndex(MediaStore.Audio.Media.DATA))

                        val albumname = cursor!!.getString(cursor!!
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM))*/
                        var same = false
                        for (i in 0 until music_name_list.size)
                        {
                            if (songname == music_name_list.get(i))
                                same = true
                        }
                        if (!same)
                        {
                            music_name_list.add(songname)
                            Log.e(TAG, songname.toString())
                        }


                    } while (cursor!!.moveToNext())
                }
                cursor!!.close()
            }
        }
    }


    fun isSdPresent(): Boolean {
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }


    private fun playSearchArtist(artist: String) {
    val intent: Intent =  Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
    intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                    MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
    intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, artist);
    intent.putExtra(SearchManager.QUERY, artist);
    if(searchMusicFolder(artist)){
        val pkg = searchAppPackageName("Music")
        if(pkg!=null){
            val launchIntent = getPackageManager().getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                startActivity(launchIntent)//null pointer check in case package name was not found
            }
        }
        else{
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
    else if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
    }
}
    private fun searchMusicFolder(songname: String): Boolean
    {
        for(i in 0 until music_name_list.size){
            try
            {
                if (music_name_list.get(i).contains(songname,true))
                {
                    Toast.makeText(this,"This works!",Toast.LENGTH_LONG).show()
                    return true
                }
            }
            catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(this,"No such song in Music folder",Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    private fun searchAppPackageName(appName: String): String{
        for(i in 0 until app_name_list.size){
            try
            {
                if (app_name_list.get(i).compareTo(appName,true)==0)
                {
                    Toast.makeText(this,"This is Music app!",Toast.LENGTH_LONG).show()
                    Log.e(TAG,"pkg name: "+app_package_list.get(i))
                    return app_package_list.get(i)
                }
            }
            catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(this,"No such APP",Toast.LENGTH_LONG).show()
            }
        }
        return "Null"
    }

    private fun sendMessageIntent(message: String){
        val uri: Uri = Uri.parse("smsto:" + number)
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
        Log.e(TAG,"Message = " + message)
        smsIntent.putExtra("sms_body", message)
        startActivity(smsIntent)
    }

    private fun loadContacts(name: String):String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        else {
            val number = getContactNumber(name)
            Log.e(TAG,"Number = " + number)
            return if(number.isNotEmpty())
                number
            else
                ""
        }
        return ""
    }

    private fun getContactNumber(name: String): String{
        val resolver: ContentResolver = contentResolver

        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

            if (displayName.contains(name,ignoreCase = true)) {
                val phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)

                while (phones.moveToNext()) {
                    val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    //Toast.makeText(this, "Number " + phoneNumber, Toast.LENGTH_LONG).show()
                    phones.close()
                    cursor.close()
                    return phoneNumber
                }
            }
        }


        Toast.makeText(this, "Name not found", Toast.LENGTH_LONG).show()
        cursor.close()
        return ""
    }

    private fun showProgressDialog(){
        progressDialog = ProgressDialog(this)
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
