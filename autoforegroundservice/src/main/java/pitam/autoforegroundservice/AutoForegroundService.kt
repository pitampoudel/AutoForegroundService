package pitam.autoforegroundservice

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService

abstract class AutoForegroundService(private val notificationId: Int) : LifecycleService() {
    private var bindCount = 0
    private fun handleBind() {
        bindCount++
        startService(Intent(this, this::class.java))
    }

    override fun onBind(intent: Intent): IBinder? {
        handleBind()
        return super.onBind(intent)
    }

    override fun onRebind(intent: Intent?) {
        handleBind()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        manageLifetime()
        // In case we are stopped by the system, have the system restart this service so we can
        // manage our lifetime appropriately.
        return START_STICKY
    }


    override fun onUnbind(intent: Intent?): Boolean {
        bindCount--
        manageLifetime()
        // Allow clients to rebind, in which case onRebind will be called.
        return true
    }

    private var notification: Notification? = null
    fun updateNotification(notification: Notification) {
        this.notification = notification
        if (isForeground) {
            startForeground(notificationId, this.notification)
        }
    }

    private var isForeground = false
    private fun manageLifetime() {
        when {
            bindCount > 0 -> {
                isForeground = false
                stopForeground(STOP_FOREGROUND_REMOVE)
            }

            notification != null -> {
                isForeground = true
                startForeground(notificationId, notification)
            }

            else -> {
                stopSelf()
            }
        }
    }

}