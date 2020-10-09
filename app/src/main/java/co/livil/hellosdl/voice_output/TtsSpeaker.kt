package co.livil.hellosdl.voice_output

import android.content.Context
import android.content.Intent
import android.util.Log
import com.smartdevicelink.managers.SdlManager
import com.smartdevicelink.proxy.RPCResponse
import com.smartdevicelink.proxy.rpc.Speak
import com.smartdevicelink.proxy.rpc.SpeakResponse
import com.smartdevicelink.proxy.rpc.TTSChunk
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener
import com.smartdevicelink.proxy.rpc.enums.Result as SdlResult


class TtsSpeaker(private val context: Context?, private val sdlManager: SdlManager?) {

    private val TAG = "TtsSpeaker"

    fun speak(text: String) {
        Log.i(TAG, "Speaking $text")
        val ttsChunk = TTSChunk(text, SpeechCapabilities.TEXT)
        val speaker = Speak(listOf(ttsChunk))
        speaker.onRPCResponseListener = object : OnRPCResponseListener() {
            override fun onResponse(correlationId: Int, response: RPCResponse) {
                val speakResponse = response as SpeakResponse

                sendResponse(speakResponse.success)
                Log.i(TAG, "Speech was successfully spoken")
            }

            private fun sendResponse(success: Boolean?) {
                val serviceIntent = Intent("co.livil.dailybriefing.tts.interaction.over")
                serviceIntent.setPackage("de.semvox.voiceinterface")
                serviceIntent.putExtra("hadError", success)
                serviceIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                context?.applicationContext?.sendBroadcast(serviceIntent)

            }

            override fun onError(correlationId: Int, resultCode: SdlResult?, info: String) {
                Log.i(TAG, "onError: $info")
                sendResponse(false)
            }
        }
        sdlManager?.sendRPC(speaker)
    }

    companion object {
        val TTS_ACTION = "tts-received"
        val TTS_TEXT = "text"
    }
}