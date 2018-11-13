package ai.rotor.vehicle

import android.app.IntentService
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import java.util.*

class BTListenForConnectionIS: IntentService("BTListenForConnectionIS"){

    override fun onHandleIntent(intent: Intent?) {

        //Make device discoverable

        val socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("rotor.ai", UUID.fromString("4204ff84-190d-4cce-9e98-526915402758"))
        val result = socket.accept()

        socket.close()

    }

    companion object {
        fun makeIntent(c: Context) = Intent(c, BTListenForConnectionIS::class.java)

        val VEHICLE_NAME = "Stus Car"
    }

}