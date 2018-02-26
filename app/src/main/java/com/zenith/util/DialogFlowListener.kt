package com.zenith.util

import ai.api.AIListener
import ai.api.model.AIError
import ai.api.model.AIResponse

/**
 * Created by gurpreet on 26/2/18.
 */
class DialogFlowListener : AIListener {
    override fun onResult(result: AIResponse?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAudioLevel(level: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: AIError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningCanceled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListeningFinished() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}