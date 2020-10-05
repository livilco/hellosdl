package co.livil.hellosdl

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Intent(this, SdlService::class.java).also { intent ->
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        requireViewById<Button>(R.id.update_view_btn).setOnClickListener { onClick() }
    }

    fun onClick() {
       Intent(this, SdlService::class.java).also {intent ->
           intent.action = "change_view"
           startService(intent)
       }
    }
}