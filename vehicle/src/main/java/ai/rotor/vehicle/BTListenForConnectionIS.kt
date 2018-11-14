package ai.rotor.vehicle

import ai.rotor.commonstuff.RotorUtils
import android.app.IntentService
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent

class BTListenForConnectionIS: IntentService("BTListenForConnectionIS"){

    override fun onHandleIntent(intent: Intent?) {

        //Make device discoverable

        val socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("rotor.ai", RotorUtils.ROTORAI_UUID)
        val result = socket.accept()

        socket.close()

    }

    companion object {
        fun makeIntent(c: Context) = Intent(c, BTListenForConnectionIS::class.java)

        val VEHICLE_NAME = "Stus Car (RTR001)"
    }

}