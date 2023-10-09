package pitam.autoforegroundservice.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject

class DemoServiceConnection @Inject constructor() : ServiceConnection {

    private var service: DemoService? = null

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as DemoService.LocalBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        // Note: this should never be called since the service is in the same process.
        service = null
    }
}