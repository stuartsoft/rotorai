package com.stuartsoft.rotorai.ui.welcome

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.os.Bundle

import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.ui.BaseActivity
import com.stuartsoft.rotorai.ui.welcome.WelcomeFragment.WelcomeFragmentHost

import javax.inject.Inject

class WelcomeActivity : BaseActivity(), WelcomeFragmentHost {
    @Inject lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: WelcomeActivityBinding

    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    val br: BroadcastReceiver = object: BroadcastReceiver() {
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

        viewModel.bluetoothRadioIsOn = BluetoothAdapter.getDefaultAdapter().isEnabled
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()

        registerReceiver(br, filter)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(br)
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }
}
