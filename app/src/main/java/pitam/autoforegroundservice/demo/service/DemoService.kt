package pitam.autoforegroundservice.demo.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import pitam.autoforegroundservice.demo.MainActivity
import pitam.autoforegroundservice.demo.R
import pitam.autoforegroundservice.AutoForegroundService
import pitam.autoforegroundservice.ForegroundNotification
import pitam.autoforegroundservice.NotificationUtils.buildNotification

@AndroidEntryPoint
class DemoService : AutoForegroundService() {

    inner class LocalBinder : Binder() {
        fun getService() = this@DemoService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        updateNotification(
            ForegroundNotification(
                id = 1,
                notification = buildNotification(
                    channelId = "CHANNEL_ID_DEMO",
                    channelName = "Demo Channel",
                    channelImportance = NotificationManager.IMPORTANCE_LOW,
                    notificationIcon = R.drawable.ic_launcher_foreground,
                    notificationTitle = "Service is running",
                    notificationText = "It will never stop"
                ) {

                    // Tapping the notification opens MainActivity
                    val startActivityIntent = PendingIntent.getActivity(
                        this@DemoService,
                        0,
                        Intent(this@DemoService, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setContentIntent(startActivityIntent)
                })
        )


        //TODO whatever you want to do
        //TODO  stopSelf() when work is completed


        return super.onStartCommand(intent, flags, startId)
    }
}


