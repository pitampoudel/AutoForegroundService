package pitam.autoforegroundservice

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService

data class ForegroundNotification(val id: Int, val notification: Notification)

abstract class AutoForegroundService : LifecycleService() {
    @Volatile
    private var bindCount = 0
        set(value) {
            field = value
            manageServiceLifecycle()
        }

    private fun handleBind() {
        synchronized(this) {
            bindCount++
        }
        startService(Intent(this, this::class.java))
    }

    override fun onBind(intent: Intent): IBinder? {
        handleBind()
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        synchronized(this) {
            bindCount--
        }
        return true // Allows clients to rebind, triggering onRebind
    }

    override fun onRebind(intent: Intent?) {
        handleBind()
    }

    private var isForeground = false

    // The notification to be displayed when in the foreground
    private var currentNotification: ForegroundNotification? = null

    fun updateNotification(notification: ForegroundNotification) {
        Log.d(this::class.simpleName,"Updating notification")
        currentNotification = notification
        if (isForeground) {
            startForeground(notification.id, notification.notification)
        } else {
            manageServiceLifecycle()
        }
    }

    private fun manageServiceLifecycle() {
        Log.d(this::class.simpleName,"Managing lifecycle, bindCount: $bindCount")
        synchronized(this) {
            val currentNotification = currentNotification
            when {
                bindCount > 0 -> {
                    // Clients are bound, stop foreground if active
                    if (isForeground) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        isForeground = false
                    }
                }

                currentNotification != null -> {
                    // No clients bound but notification is available, start foreground
                    Log.d(this::class.simpleName,"Starting foreground")
                    startForeground(currentNotification.id, currentNotification.notification)
                    isForeground = true
                }

                else -> {
                    // No clients and no notification, stop the service
                    stopSelf()
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        manageServiceLifecycle()
        return START_STICKY
    }


}
