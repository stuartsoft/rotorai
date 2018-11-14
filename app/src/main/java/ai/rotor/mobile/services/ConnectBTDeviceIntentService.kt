package ai.rotor.mobile.services

import ai.rotor.commonstuff.GenericBTDevice
import ai.rotor.commonstuff.RotorUtils
import android.app.IntentService
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log

class ConnectBTDeviceIntentService: IntentService("ConnectBTDeviceIntentService"){

    override fun onHandleIntent(intent: Intent?) {
        val deviceToConnectTo = intent?.getParcelableExtra<GenericBTDevice>("DEVICE_TO_CONNECT_TO")

        val dev = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceToConnectTo!!.address)

        val socket = dev.createRfcommSocketToServiceRecord(RotorUtils.ROTORAI_UUID)

        socket.connect()

        Log.d("STUDEBUG ", "FINISHED connecting")
    }

    companion object {
        fun makeIntent(c: Context, genericBTDevice: GenericBTDevice) = Intent(c, ConnectBTDeviceIntentService::class.java).also { it.putExtra("DEVICE_TO_CONNECT_TO", genericBTDevice) }
    }

}