package com.zenith.activity

import ai.api.AIConfiguration.SupportedLanguages
import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIConfiguration.RecognitionEngine
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.zenith.R


class MainActivity : AppCompatActivity(), AIListener {


    override fun onResult(result: AIResponse) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val res = result.getResult()
        Toast.makeText(this, "Result= "+res, Toast.LENGTH_LONG).show()

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
