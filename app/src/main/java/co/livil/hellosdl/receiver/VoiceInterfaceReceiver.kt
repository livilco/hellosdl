package co.livil.hellosdl.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import co.livil.hellosdl.voice_output.TtsSpeaker

class VoiceInterfaceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("VoiceInterfaceReceiver", "received intent $intent")
        val speechIntent = Intent(TtsSpeaker.TTS_ACTION)
        speechIntent.putExtra(TtsSpeaker.TTS_TEXT, intent!!.getStringExtra("text"))
        context?.sendBroadcast(speechIntent)
    }
}