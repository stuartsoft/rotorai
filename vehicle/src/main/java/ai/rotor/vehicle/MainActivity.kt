package ai.rotor.vehicle

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import com.google.android.things.bluetooth.BluetoothConnectionManager
import com.google.android.things.bluetooth.BluetoothPairingCallback
import com.google.android.things.bluetooth.PairingParams
import timber.log.Timber

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.d("STUDEBUG - Starting up")
        BluetoothAdapter.getDefaultAdapter().name = BTListenForConnectionIS.VEHICLE_NAME


        val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!btAdapter.isEnabled) {
            turnBTRadioON()
        }
        else {
            startDiscoverability()
        }

        BluetoothConnectionManager.getInstance().registerPairingCallback(object : BluetoothPairingCallback {
            override fun onUnpaired(bluetoothDevice: BluetoothDevice?) {
            }

            override fun onPairingError(bluetoothDevice: BluetoothDevice?, error: BluetoothPairingCallback.PairingError?) {
            }

            override fun onPairingInitiated(bluetoothDevice: BluetoothDevice?, pairingParams: PairingParams?) {
                val okToPair = true
                if (okToPair) {
                    BluetoothConnectionManager.getInstance().finishPairing(bluetoothDevice)
                }
            }

            override fun onPaired(bluetoothDevice: BluetoothDevice?) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TURN_BT_ON) {
            //START DISCOVERABILITY...
            startDiscoverability()
        }
        else if (requestCode == REQUEST_MAKE_DISCOVERABLE){
            //Open a btSocket on an intent service, awaiting connection...
            startService(BTListenForConnectionIS.makeIntent(this))
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun turnBTRadioON(){
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_TURN_BT_ON)
    }

    private fun startDiscoverability() {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply { putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60) }, REQUEST_MAKE_DISCOVERABLE)
    }

    companion object {
        val REQUEST_TURN_BT_ON = 1
        val REQUEST_MAKE_DISCOVERABLE = 2
    }
}
