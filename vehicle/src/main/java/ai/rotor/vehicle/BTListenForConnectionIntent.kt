package ai.rotor.vehicle

import android.app.IntentService
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.util.*

class BTListenForConnectionIntent: IntentService("MahFancyIntentService"){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("STUDEBUG - Starting intent service")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {

        val socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("STUs RPI3", UUID.randomUUID())

        //Make device discoverable
        startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply { putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) })

        Timber.d("STUDEBUG - now accepting connections")

        val result = socket.accept()

        Timber.d("STUDEBUG - " + result.remoteDevice.toString())

        socket.close()

    }

    companion object {
        fun makeIntent(c: Context) = Intent(c, BTListenForConnectionIntent::class.java)
    }

}