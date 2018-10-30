package com.stuartsoft.rotorai.ui.welcome

import androidx.lifecycle.Observer
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.ui.BaseActivity
import com.stuartsoft.rotorai.ui.welcome.WelcomeFragment.WelcomeFragmentHost

import javax.inject.Inject

class WelcomeActivity : BaseActivity(), WelcomeFragmentHost {
    @Inject lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: WelcomeActivityBinding

    val REQUEST_TURN_BT_ON = 1
    val REQUEST_ENABLE_LOCATION_PERMISSION = 2

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
        supportActionBar!!.title = "RotorAI"


        //Add mah intent filters
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(br, btFilter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()

        viewModel.shouldShowBTDialog.observe(this, Observer<Boolean> {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_TURN_BT_ON)
        })

        viewModel.shouldAskForLocationDialog.observe(this, Observer<Boolean> {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_ENABLE_LOCATION_PERMISSION)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.notifyChange()
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
