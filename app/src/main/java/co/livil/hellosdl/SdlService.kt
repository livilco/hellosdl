package co.livil.hellosdl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.smartdevicelink.managers.SdlManager
import com.smartdevicelink.managers.SdlManagerListener
import com.smartdevicelink.managers.lifecycle.LifecycleConfigurationUpdate
import com.smartdevicelink.proxy.RPCResponse
import com.smartdevicelink.proxy.rpc.SetDisplayLayout
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse
import com.smartdevicelink.proxy.rpc.enums.AppHMIType
import com.smartdevicelink.proxy.rpc.enums.Language
import com.smartdevicelink.proxy.rpc.enums.PredefinedLayout
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener
import com.smartdevicelink.transport.TCPTransportConfig
import java.util.*

class SdlService : Service() {
    private var sdlManager : SdlManager? = null

    override fun onCreate() {
        super.onCreate()

        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SDL Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        val serviceNotification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("SDL Service")
            .setSmallIcon(R.drawable.ic_sdl)
            .setChannelId(channel.id)
            .build()

        startForeground(NOTIFICATION_ID, serviceNotification)
    }

    override fun onDestroy() {
       super.onDestroy()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager?)?.deleteNotificationChannel(CHANNEL_ID)
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (sdlManager == null) {
            val transport = TCPTransportConfig(
                19844,
                "m.sdl.tools",
                false
            )

            val appType = Vector<AppHMIType>()
            appType.add(AppHMIType.MEDIA)

            val listener = object: SdlManagerListener {
                override fun onStart() {
                    Log.d("SdlService", "Service started")
                }

                override fun onDestroy() {
                    this@SdlService.stopSelf()
                }

                override fun onError(info: String?, e: Exception?) {
                    Log.e("SdlService", info)
                    if (e != null) {
                        Log.e("SdlService", e.localizedMessage)
                    }
                }

                override fun managerShouldUpdateLifecycle(language: Language?): LifecycleConfigurationUpdate {
                    TODO("Not yet implemented")
                }

                override fun managerShouldUpdateLifecycle(
                    language: Language?,
                    hmiLanguage: Language?
                ): LifecycleConfigurationUpdate {
                    TODO("Not yet implemented")
                }
            }

            // val appIcon = SdlArtwork(APP_ICON, FileType.GRAPHIC_PNG, R.mipmap.ic_launcher, true)

            val builder = SdlManager.Builder(this, APP_ID, APP_NAME, listener)
            builder.setAppTypes(appType)
            builder.setTransportType(transport)
            // builder.setAppIcon(appIcon)

            sdlManager = builder.build()
            sdlManager!!.start()
        }

        when (intent?.action) {
            "change_view" -> changeView()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun changeView() {
        val setDisplayLayoutRequest = SetDisplayLayout().also {
            it.displayLayout = PredefinedLayout.GRAPHIC_WITH_TEXT.toString()
            it.onRPCResponseListener = object : OnRPCResponseListener() {
                override fun onResponse(correlationId: Int, response: RPCResponse?) {
                    if ((response as SetDisplayLayoutResponse).success) {
                        Log.i("SdlService", "Display layout set successfully.")
                        updateView()
                    } else {
                        Log.e("SdlService", "onError: ${response.resultCode} | Info: ${response.info}")
                    }
                }
            }
        }

        sdlManager?.sendRPC(setDisplayLayoutRequest)
    }

    private fun updateView() {
        sdlManager?.screenManager!!.also {
            it.beginTransaction()
            it.setTextField1("Good Morning")
            it.setTextField2("Mr Bond")
            it.commit { success ->
                Log.i("SdlService#updateView", "ScreenManager update complete?: $success")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        val APP_ID = "abcd1234"
        val CHANNEL_ID = "sdl_service"
        val NOTIFICATION_ID = 123456
        val APP_ICON = ""
        val APP_NAME = "Hello SDL!"
    }
}