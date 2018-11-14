package ai.rotor.mobile.ui.welcome

import ai.rotor.mobile.R
import ai.rotor.mobile.services.ConnectBTDeviceIntentService
import ai.rotor.mobile.ui.BaseActivity
import ai.rotor.mobile.ui.welcome.WelcomeFragment.WelcomeFragmentHost
import ai.rotor.mobile.ui.welcome.WelcomeViewModel.Companion.REQUEST_ENABLE_LOCATION_PERMISSION
import ai.rotor.mobile.ui.welcome.WelcomeViewModel.Companion.REQUEST_TURN_BT_ON
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import javax.inject.Inject

class WelcomeActivity : BaseActivity(), WelcomeFragmentHost {
    @Inject lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: WelcomeActivityBinding

    private val btFilter = IntentFilter()
    val br = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.onReceiveBroadcast(intent)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(WelcomeViewModel::class)
        viewModel.restoreState(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.vm = viewModel
        binding.executePendingBindings()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.app_name)

        //Add mah intent filters
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(br, btFilter)

        viewModel.shouldShowBTDialog.observe(this, Observer {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_TURN_BT_ON)
        })

        viewModel.shouldAskForLocationDialog.observe(this, Observer {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_ENABLE_LOCATION_PERMISSION)
        })

        viewModel.readyToStartRemoteControl.observe(this, Observer {
            startService(ConnectBTDeviceIntentService.makeIntent(this, it))
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }
}
