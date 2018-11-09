package ai.rotor.vehicle

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import timber.log.Timber
import java.util.*

class BTListenForConnectionIS: IntentService("BTListenForConnectionIS"){

    override fun onHandleIntent(intent: Intent?) {

        //Make device discoverable

        BluetoothAdapter.getDefaultAdapter().name = VEHICLE_NAME

        val socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("", UUID.randomUUID())

        Log.d("STUDEBUG ","now accepting connections")

        val result = socket.accept()

        Log.d("STUDEBUG - ", result.remoteDevice.toString())

        socket.close()

    }

    companion object {
        fun makeIntent(c: Context) = Intent(c, BTListenForConnectionIS::class.java)

        val VEHICLE_NAME = "Stus Car"
    }

}